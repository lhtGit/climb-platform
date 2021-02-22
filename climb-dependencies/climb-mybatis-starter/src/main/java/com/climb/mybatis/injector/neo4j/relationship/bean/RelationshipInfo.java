package com.climb.mybatis.injector.neo4j.relationship.bean;


import lombok.Data;

import java.util.Set;

/**
 * 存储关联信息
 * @author lht
 * @since 2021/2/22 09:29
 */
@Data
public class RelationshipInfo {

    private RelTableInfo formTableInfo;

    private RelTableInfo toTableInfo;

    private Set<String> relationshipFields;

    /**
     * 是否正常
     * @author lht
     * @since  2021/2/22 9:32
     */
    public boolean isSuccess(){
        return formTableInfo != null && toTableInfo != null;
    }

}
