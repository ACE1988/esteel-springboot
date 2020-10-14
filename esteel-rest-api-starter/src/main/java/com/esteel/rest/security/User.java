package com.esteel.rest.security;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static com.esteel.common.utils.NumberUtil.isNumerical;

@Data
public class User extends org.springframework.security.core.userdetails.User {

  private String id;
  private Long idLong;
  private Integer idInt;
  private String displayName;

  private boolean numericalId;

  public User(String userId, Collection<? extends GrantedAuthority> authorities) {
    super(userId, "", authorities);
    this.id = userId;

    this.numericalId = isNumerical(this.id);
  }

  public Long getIdLong() {
    if (idLong == null && numericalId) {
      idLong = Long.parseLong(id);
    }
    return idLong;
  }

  public Integer getIdInt() {
    if (idInt == null && numericalId) {
      idInt = Integer.parseInt(id);
    }
    return idInt;
  }

  public boolean contains(String authority) {
    if (getAuthorities() == null || StringUtils.isBlank(authority)) {
      return false;
    }
    for (GrantedAuthority grantedAuthority : getAuthorities()) {
      if (grantedAuthority.getAuthority().equals(authority)) {
        return true;
      }
    }
    return false;
  }
}
