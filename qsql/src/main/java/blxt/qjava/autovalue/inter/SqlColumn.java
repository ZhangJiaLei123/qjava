package blxt.qjava.autovalue.inter;

import blxt.qjava.autovalue.bean.JoinEnum;
import lombok.Data;

import java.lang.annotation.*;

/**
 * SqlColumn 字段定义
 * @author ZhangJieLei
 */
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SqlColumn {
    /** 字段值 */
    String value() default "";
    /** 类型 */
    String method() default "";
    /** 是否可以是null */
    boolean isNull() default true;
    /** 默认值 */
    String defaultValue() default "";
    /** 字段描述 */
    String comment() default "";

    /** 函数 */
    String function() default "";

    /** 模糊查询 */
    @Deprecated
    boolean selectLike() default false;

    /** select 中可见 */
    boolean selectEnable() default true;
    /** 自增序列*/
    boolean sequence() default false;
    /** 自动更新触发 */
    boolean autoupdate() default false;

}
