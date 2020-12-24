```

##引入宏定义
$!define

####定义初始变量
###set($tableName = $tool.append($tableInfo.name, "PO"))
##
####设置回调
##$!callback.setFileName($tool.append($tableName, ".java"))
##$!callback.setSavePath($tool.append($tableInfo.savePath, "/entity"))
####使用宏定义设置回调（保存位置与文件后缀）
#save("/entity", ".java")

##使用宏定义设置包后缀
#setPackageSuffix("entity")

##使用全局变量实现默认包导入
$!autoImport
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;


/**
 * $!{tableInfo.comment}($!{tableInfo.name})
 *
 * @author $!author
 * @since $!time.currTime()
 */
@Data
@TableName("$!{tableInfo.obj.name}")
public class $!{tableInfo.name} implements Serializable {
    private static final long serialVersionUID = $!tool.serial();
    
#foreach($column in $tableInfo.pkColumn)
    #if(${column.comment})/**
    * ${column.comment} 
    */#end
    #if($column.name.startsWith("is") && $column.type.equals("java.lang.Boolean"))
        $!column.setName($tool.firstLowerCase($column.name.substring(2)))
    #end
    @TableId("${column.obj.name}")
    private $!{tool.getClsNameByFullName($column.type)} $!{column.name};
#end



#foreach($column in $tableInfo.otherColumn)
    #if(${column.comment})/**
    * ${column.comment} 
    */#end
    #if($column.name.startsWith("is") && $column.type.equals("java.lang.Boolean"))
        $!column.setName($tool.firstLowerCase($column.name.substring(2)))
    #end
    @TableField("${column.obj.name}")
    private $!{tool.getClsNameByFullName($column.type)} $!{column.name};
#end


###foreach($column in $tableInfo.fullColumn)
##    ##使用宏定义实现get,set方法
##    #getSetMethod($column)
###end

}
```