package blxt.qjava.autovalue.inter;

import java.lang.annotation.*;

/**
 * SqlColumn 字段定义
 * @author ZhangJieLei
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface SqlColumn {
    /** 字段值 */
    String value() default "";
    /** 表名 */
    String table() default "";
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

    /** select 中可见 */
    boolean selectEnable() default true;

    /** 查询条件 中使用 */
    boolean whereEnable() default true;

    /** install 中可见 */
    boolean installEnable() default true;

    /** 自增序列*/
    boolean sequence() default false;
    /** 自动更新触发 */
    boolean autoupdate() default false;

    /** 长度 */
    int leng() default -1;

}
