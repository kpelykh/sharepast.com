<#---------------------------------------------------------------------------->
<#-- Init util.account and util.loggedIn -->
<#---------------------------------------------------------------------------->
<#assign userMap=methods.getUser() />

<#if userMap.user??>
  <#global user=userMap.user />
</#if>


<#-- helper, use util.loggedIn to check if the current user is logged in -->
<#assign isAuthenticated=userMap.isAuthenticated >
<#assign isRemembered=userMap.isRemembered>
<!-- example of rreferencing static field from ftl -->
<#--<#if .data_model[statics["com.sharepast.constants.LogonConstants"].LOGOUT_FLAG]??>
    <#assign isLoggedOut = true>
</#if>-->



<#-- extracts Url query params as Map -->
<#function getQueryParams>
  <#local res={}/>
  <#if (HTTPRequest.originalRef)??>
    <#local indx=HTTPRequest.originalRef?index_of("?")/>
    <#if (indx>=0)>
      <#local query=HTTPRequest.originalRef?substring(indx+1)/>
      <#if query?has_content>
        <#list query?split("&") as pair>
          <#if pair?has_content>
            <#local indx=pair?index_of("=")/>
            <#if (indx>0)>
              <#local res=res+{pair?substring(0,indx):pair?substring(indx+1)}/>
            </#if>
          </#if>
        </#list>
      </#if>
    </#if>
  </#if>
  <#return res/>
</#function>

<#-- returns value for the query parameter -->
<#function getQueryParam pname>
  <#local params=getQueryParams()/>
  <#if (params[pname])??>
    <#return params[pname]/>
  <#else>
    <#return ""/>
  </#if>
</#function>



<#function getRequestedURL>
  <#local url=""/>
  <#if HTTPRequest??>
    <#local url=HTTPRequest.originalRef/>
    <#local rootRef=HTTPRequest.rootRef/>
    <#local indx=url?index_of("?")/>
    <#if (indx>=0)><#local url=url?substring(0,indx)/></#if>
    <#local url=url?substring(rootRef?length)/>
    <#return url/>
  </#if>
</#function>



<#-- builds url query string using current url and adds/replaces/removes values from inp -->
<#function getCurrentUrlWithParams inp={}>
  <#local url=""/>
  <#if HTTPRequest??>
    <#local params=getQueryParams()/>
    <#local url=HTTPRequest.originalRef/>
    <#local indx=url?index_of("?")/>
    <#if (indx>=0)><#local url=url?substring(0,indx)/></#if>

    <#local items={}/>
    <#list params?keys as key>
      <#if !(inp[key])??>
        <#local items=items+{key:params[key]}/>
      </#if>
    </#list>
    <#list inp?keys as key>
      <#if (inp[key])?has_content>
        <#local items=items+{key:inp[key]}/>
      </#if>
    </#list>

    <#local separator="?"/>
    <#list items?keys as key>
      <#local url=url+separator+key+"="+items[key]/>
      <#local separator="&"/>
    </#list>
  </#if>
  <#return url/>
</#function>

<#function getUserProfileUrl user>
  <#return methods.url("/profile/{" + enums.ParamNameEnum.USER_ID.key + "}", {enums.ParamNameEnum.USER_ID.key: user.id?c}) />
</#function>

