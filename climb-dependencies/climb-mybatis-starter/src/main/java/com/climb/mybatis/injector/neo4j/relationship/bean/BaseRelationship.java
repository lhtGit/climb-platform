package com.climb.mybatis.injector.neo4j.relationship.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lht
 * @since 2021/2/14 14:39
 */
@Setter
@Getter
public class BaseRelationship {

    @TableField("formId")
    private String formId;

    @TableField("toId")
    private String toId;


}
