package utils.module;

import org.sunbird.common.util.JsonKey;
import play.libs.typedmap.TypedKey;

public class Attrs {
  public static final TypedKey<String> USERID = TypedKey.<String>create(JsonKey.USER_ID);
  public static final TypedKey<String> CONTEXT = TypedKey.<String>create(JsonKey.CONTEXT);
  public static final TypedKey<String> MANAGED_FOR = TypedKey.<String>create(JsonKey.MANAGED_FOR);
  public static final TypedKey<String> START_TIME = TypedKey.<String>create(JsonKey.START_TIME);
  public static final TypedKey<String> X_REQUEST_ID = TypedKey.<String>create(JsonKey.X_REQUEST_ID);
}
