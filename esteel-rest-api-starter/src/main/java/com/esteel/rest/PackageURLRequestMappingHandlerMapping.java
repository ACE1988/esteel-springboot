package com.esteel.rest;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.Set;

public class PackageURLRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

  public static final String ROOT_URL_PREFIX = "/api";

  private String rootUrlPrefix;

  private Set<String> packageBases;

  /*
   * format: $old1:$new1|$old2:$new2
   */
  private String replacement = "v:|_:.|";

  public void setRootUrlPrefix(String rootUrlPrefix) {
    this.rootUrlPrefix = StringUtils.isBlank(rootUrlPrefix) ? null : rootUrlPrefix.trim();
  }

  public String getRootUrlPrefix() {
    return StringUtils.isBlank(rootUrlPrefix) ? ROOT_URL_PREFIX : rootUrlPrefix;
  }

  /**
   * 包名前缀, 即包名截取该值后开始转换成url的path
   */
  public void setPackageBases(String packageBases) {
    if (StringUtils.isBlank(packageBases)) {
      this.packageBases = Sets.newHashSet();
      return;
    }
    String[] splits = packageBases.split(",");
    this.packageBases = Sets.newHashSet(splits);
  }

  public void setReplacement(String replacement) {
    this.replacement = replacement;
  }

  @Override
  protected HandlerMethod createHandlerMethod(Object handler, Method method) {
    return super.createHandlerMethod(handler, method);
  }

  protected RequestMappingInfo createRequestMappingInfo(String pattern) {
    String[] patterns = (null == pattern) ? null
        : this.resolveEmbeddedValuesInPatterns(new String[]{pattern});
    return new RequestMappingInfo(new PatternsRequestCondition(patterns,
        this.getUrlPathHelper(), this.getPathMatcher(),
        this.useSuffixPatternMatch(), this.useTrailingSlashMatch(),
        this.getFileExtensions()), null, null, null, null, null,
        null);
  }

  @Override
  protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
    RequestMappingInfo info = super.getMappingForMethod(method, handlerType);
    String prefix = getPrefix(handlerType);
    if (prefix == null) {
      return info;
    }
    if (info != null) {
      info = createRequestMappingInfo(prefix).combine(info);
    }
    return info;
  }

  protected String getPrefix(Class<?> handlerType) {
    String matchedPackageBase = getMatchedPackage(handlerType);
    if (StringUtils.isBlank(matchedPackageBase)) {
      return null;
    }

    StringBuilder prefix = new StringBuilder(getRootUrlPrefix());
    Package pack = handlerType.getPackage();
    String fullName = pack.getName();
    String pbase = matchedPackageBase;

    if (StringUtils.containsIgnoreCase(handlerType.toString(), "login")) {
      String[] names = fullName.split("\\.");
      String name = names[names.length - 1];
      name = this.replace(name);
      prefix.append('/');
      prefix.append(name);
    } else if (null != pbase && !pbase.isEmpty() && 0 == fullName.indexOf(pbase)) {
      pbase = pbase.endsWith(".") ? pbase : (pbase += '.');
      fullName = fullName.substring(pbase.length());
      String[] names = fullName.split("\\.");
      for (String name : names) {
        name = this.replace(name);
        prefix.append('/');
        prefix.append(name);
      }
    }

    return prefix.toString().replace("//", "/");
  }

  protected boolean isMatchPackageBase(Class<?> handlerType) {
    return StringUtils.isNotBlank(getMatchedPackage(handlerType));
  }

  private String getMatchedPackage(Class<?> handlerType) {
    String matchedPackageBase = null;
    for (String packageBase : packageBases) {
      Package pack = handlerType.getPackage();
      if (pack == null || StringUtils.isBlank(pack.getName())) {
        break;
      }
      if (handlerType.getPackage().getName().startsWith(packageBase)) {
        matchedPackageBase = packageBase;
        break;
      }
    }
    return matchedPackageBase;
  }

  private String replace(String str) {
    if (null != str && !str.isEmpty() && null != replacement
        && !replacement.isEmpty() && str.startsWith("v")) {
      String[] segs = replacement.split("\\|");
      for (String seg : segs) {
        String[] pairs = seg.split(":");
        String oldVal = pairs[0];
        String newVal = "";
        if (pairs.length > 1) {
          newVal = pairs[1];
        }
        str = str.replace(oldVal, newVal);
      }
    }

    return str;
  }
}