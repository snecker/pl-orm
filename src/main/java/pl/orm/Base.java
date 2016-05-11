package pl.orm;

import java.io.Serializable;

/**
 * Created by wangpeng on 2016/5/11.
 */
public class Base implements Serializable {

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
