package postgres;

import blxt.qjava.autovalue.inter.SqlBean;
import blxt.qjava.autovalue.inter.SqlColumn;
import lombok.Data;

@Data
@SqlBean(keyId = "uid")
public class User {
    @SqlColumn(value = "uid")
    int uid;
    @SqlColumn(value = "uname")
    String uname;
}
