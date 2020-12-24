```
##导入宏定义
$!define

##设置表后缀（宏定义）
#setTableSuffix("Controller")

##保存文件（宏定义）
#save("/controller", "Controller.java")

##包路径（宏定义）
#setPackageSuffix("controller")

##定义服务名
#set($serviceName = $!tool.append($!tool.firstLowerCase($!tableInfo.name), "Service"))

##定义实体对象名
#set($entityName = $!tool.firstLowerCase($!tableInfo.name))

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gglc.common.controller.BaseController;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import $!{tableInfo.savePackageName}.entity.$!tableInfo.name;
import $!{tableInfo.savePackageName}.service.$!{tableInfo.name}Service;
import org.springframework.web.bind.annotation.*;
import com.gglc.common.bean.Result;
import com.gglc.common.bean.PageResult;
import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


##表注释（宏定义）
#tableComment("表控制层")
@Api(tags = "$!{tableInfo.comment} Controller")
@RestController
@RequestMapping("$!tool.firstLowerCase($!tableInfo.name)")
public class $!{tableName} extends BaseController {
    /**
     * 服务对象
     */
    @Resource
    private $!{tableInfo.name}Service $!{serviceName};

    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param $!entityName 查询实体
     * @return 所有数据
     */
    @GetMapping
    @ApiOperation("$!{tableInfo.comment} 分页查询所有数据")
    public PageResult<$!tableInfo.name> selectAll(Page<$!tableInfo.name> page, $!tableInfo.name $!entityName) {
        return successPage(this.$!{serviceName}.page(page, new QueryWrapper<>($!entityName)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    @ApiOperation("$!{tableInfo.comment} 通过主键查询单条数据")
    public Result<$!tableInfo.name> selectOne(@PathVariable Serializable id) {
        return success(this.$!{serviceName}.getById(id));
    }

    /**
     * 新增数据
     *
     * @param $!entityName 实体对象
     * @return 新增结果
     */
    @PostMapping
    @ApiOperation("$!{tableInfo.comment} 新增数据")
    public Result<Boolean> insert(@RequestBody $!tableInfo.name $!entityName) {
        return success(this.$!{serviceName}.save($!entityName));
    }

    /**
     * 修改数据
     *
     * @param $!entityName 实体对象
     * @return 修改结果
     */
    @PutMapping
    @ApiOperation("$!{tableInfo.comment} 修改数据")
    public Result<Boolean> update(@RequestBody $!tableInfo.name $!entityName) {
        return success(this.$!{serviceName}.updateById($!entityName));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    @ApiOperation("$!{tableInfo.comment} 删除数据")
    public Result<Boolean> delete(@RequestParam("idList") List<Long> idList) {
        return success(this.$!{serviceName}.removeByIds(idList));
    }
}
```