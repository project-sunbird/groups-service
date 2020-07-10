package org.sunbird.sso;

import org.sunbird.sso.service.KeyCloakServiceImpl;

/** @author Amit Kumar */
public class SSOServiceFactory {
  private static SSOManager ssoManager = null;

  private SSOServiceFactory() {}

  /**
   * On call of this method , it will provide a new KeyCloakServiceImpl instance on each call.
   *
   * @return SSOManager
   */
  public static SSOManager getInstance() {
    if (null == ssoManager) {
      ssoManager = new KeyCloakServiceImpl();
    }
    return ssoManager;
  }
}