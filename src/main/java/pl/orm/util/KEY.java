package pl.orm.util;

/**
 * Created by wangpeng on 2016/5/9.
 */
public interface KEY {
    String BLANK = " ";

    enum CURD {
        INSERT,
        UPDATE,
        SELECT,
        DELETE;

        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }

}
