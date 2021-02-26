package com.climb.seata.validate;

import com.google.common.collect.Lists;
import io.seata.common.util.CollectionUtils;
import io.seata.common.util.StringUtils;
import io.seata.core.model.Result;
import io.seata.rm.datasource.DataCompareUtils;
import io.seata.rm.datasource.sql.struct.Field;
import io.seata.rm.datasource.sql.struct.Row;
import io.seata.rm.datasource.sql.struct.TableMeta;
import io.seata.rm.datasource.sql.struct.TableRecords;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 自定义数据验证 判断数据回滚前是否变更
 * 之所以要自定义是因为数据库有一些设置了默认值，当新增时为null的字段因为默认值就变为了默认值，在seata做image对比时，会因为数据前后不一致
 * 导致回滚失败，为了避免这种不正常的回滚异常，自定义一个指定字段是否一致，如果一致就回滚
 * 自定义回滚的触发条件为：当验证有脏数据时判断指定字段是否变更，如果全部一致则不执行自定义数据验证
 * @author lht
 * @since 2021/2/26 15:03
 */
@Slf4j
@RefreshScope
@Configuration
public class DataCustomizeValidation {
    @Value("${seata.data.validate.fields:}")
    private String validateFieldsString;


    private List<List<String>> validateFields = new ArrayList<>();


    @PostConstruct
    public void init(){
        if(!StringUtils.isEmpty(validateFieldsString)){
            String[] fields = validateFieldsString.trim().toUpperCase().split(";");
            validateFields = Stream.of(fields)
                    .map(fieldsStr -> Lists.newArrayList(fieldsStr.split(",")))
                    .collect(Collectors.toList());
        }
    }
    /**
     * seata 代码 copy  by DataCompareUtils
     * Is image equals.
     *
     * @param beforeImage the before image
     * @param afterImage  the after image
     * @return Result<Boolean>
     */
    public  Result<Boolean> isRecordsEquals(TableRecords beforeImage, TableRecords afterImage) {
        //如果没有设置需要验证的字段，直接返回false
        if(validateFields.isEmpty()){
            return Result.build(false);
        }
        if (beforeImage == null) {
            return Result.build(afterImage == null, null);
        } else {
            if (afterImage == null) {
                return Result.build(false, null);
            }
            if (beforeImage.getTableName().equalsIgnoreCase(afterImage.getTableName())
                    && CollectionUtils.isSizeEquals(beforeImage.getRows(), afterImage.getRows())) {
                //when image is EmptyTableRecords, getTableMeta will throw an exception
                if (CollectionUtils.isEmpty(beforeImage.getRows())) {
                    return Result.ok();
                }
                return compareRows(beforeImage.getTableMeta(), beforeImage.getRows(), afterImage.getRows());
            } else {
                return Result.build(false, null);
            }
        }
    }



    /**
     * customize validation
     * @author lht
     * @since  2021/2/26 16:50
     * @param  oldRow
     * @param  newRow
     */
    public  boolean validation(Map<String, Field> oldRow,  Map<String, Field> newRow){
        boolean boo;
        for (List<String> list : validateFields) {
            boo = true;
            for (String validateField : list) {
                Field oldField = oldRow.get(validateField);
                Field newField = newRow.get(validateField);
                //如果为null 则不作验证
                if(oldField==null){
                    boo = false;
                    break;
                }
                //如果不一致，跳出当前循环
                if(!DataCompareUtils.isFieldEquals(oldField,newField).getResult()){
                    log.warn("自定义对比失败：old value["+oldField.getValue()+"],new value["+newField.getValue()+"]");
                    boo = false;
                    break;
                }
            }
            //如果有集合全部验证成功，直接返回true
            if(boo){
                return boo;
            }
        }
        return false;
    }


    /**
     * seata 代码 copy  by DataCompareUtils
     */
    public  Map<String, Map<String, Field>> rowListToMap(List<Row> rowList, List<String> primaryKeyList) {
        // {value of primaryKey, value of all columns}
        Map<String, Map<String, Field>> rowMap = new HashMap<>();
        for (Row row : rowList) {
            //ensure the order of column
            List<Field> rowFieldList = row.getFields().stream()
                    .sorted(Comparator.comparing(Field::getName))
                    .collect(Collectors.toList());
            // {uppercase fieldName : field}
            Map<String, Field> colsMap = new HashMap<>();
            StringBuilder rowKey = new StringBuilder();
            boolean firstUnderline = false;
            for (int j = 0; j < rowFieldList.size(); j++) {
                Field field = rowFieldList.get(j);
                if (primaryKeyList.stream().anyMatch(e -> field.getName().equals(e))) {
                    if (firstUnderline && j > 0) {
                        rowKey.append("_");
                    }
                    rowKey.append(String.valueOf(field.getValue()));
                    firstUnderline = true;
                }
                colsMap.put(field.getName().trim().toUpperCase(), field);
            }
            rowMap.put(rowKey.toString(), colsMap);
        }
        return rowMap;
    }



    /**
     * seata 代码 copy  by DataCompareUtils
     * 作为自定义验证入口
     */
    private  Result<Boolean> compareRows(TableMeta tableMetaData, List<Row> oldRows, List<Row> newRows) {
        // old row to map
        Map<String, Map<String, Field>> oldRowsMap = rowListToMap(oldRows, tableMetaData.getPrimaryKeyOnlyName());
        // new row to map
        Map<String, Map<String, Field>> newRowsMap = rowListToMap(newRows, tableMetaData.getPrimaryKeyOnlyName());
        // compare data
        for (Map.Entry<String, Map<String, Field>> oldEntry : oldRowsMap.entrySet()) {
            Map<String, Field> oldRow = oldEntry.getValue();
            Map<String, Field> newRow = newRowsMap.get(oldEntry.getKey());
            if (newRow == null) {
                return Result.build(false);
            }
            if(!validation(oldRow,newRow)){
                return Result.build(false);
            }
        }
        return Result.ok();
    }


}
