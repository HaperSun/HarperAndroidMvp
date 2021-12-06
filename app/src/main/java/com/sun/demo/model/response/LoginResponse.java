package com.sun.demo.model.response;

import com.sun.base.net.response.Response;

import java.util.List;
import java.util.Map;

/**
 * @author Harper
 * @date 2021/11/12
 * note:
 */
public class LoginResponse extends Response {
    public int code;
    public String message;
    public Object object;


    public static class Object {

        public String isRiskPointOnlineState;//离线开关 0关闭 1开启
        public String inspectCycle;
        public int enterpriseId;//企业id
        public String enterpriseName;
        public int id;
        public int isSuper;
        public int isValid;
        public String loginName;
        public String organId;
        public String organName;
        public String organCode;
        public String parentCode;
        public String areaCode;
        public String appoName;
        public String password;
        public String phone;
        public String token;
        public String userName;
        public int orgLevel;
        public List<UserOrgListBean> userOrgList;
        public int basicMessage;
        public int vgaMessage;
        public String isUseCache; //0不适用  1使用
        public int isSelectPhoto;
        public int safetySupervisionDepartment;//1是安全监督部门 0 不是
        public int unitType;//登录用单位类型 1-企业 2-网格系统 3-卫生系统 4-学校系统 5-质监系统
        public String gridLevel;
        public int isChoosePhoto;//0-非强制  1-强制
        public String isHdNeedApprove;//隐患审核方式-0不需要审核 1部门负责人审核 2自定义审核人审核
        public int isOutSourcing;//是否外包人员 0 不是 1是
        /**
         * orgLevelMap : {"1":"公司级","2":"分管级","3":"部门级","4":"车间级","5":"班组级","6":"岗位级"}
         */

        public Map<Integer, String> orgLevelMap;
        //隐患区域
        public String hdAreas;
        //自定义审核人
        public List<CustomExamineBean> hdDefPersonList;

        public String isLegal = "";//是否是企业负责人  0-不是  1-是
        public String isOfficer = ""; //是否是安全负责人  0-不是  1-是
        public int isAccepPerDef = 0; //"隐患验收-验收人是否自定义 0否 1是


        public String riskPointVersion;
        public String userVersion;
        public List<SubEnterpriseListBean> subEnterpriseList;
        public String dangerSourceVersion;
        //是否强制签字 0 不强制  1强制签字
        public int isInspForceSign;
        //是否是安全监管人员：1是，0否
        public int isSafetyOfficer;

        public int getIsChoosePhoto() {
            return isChoosePhoto;
        }

        public void setIsChoosePhoto(int isChoosePhoto) {
            this.isChoosePhoto = isChoosePhoto;
        }

        public Map<Integer, String> getOrgLevelMap() {
            return orgLevelMap;
        }

        public void setOrgLevelMap(Map<Integer, String> orgLevelMap) {
            this.orgLevelMap = orgLevelMap;
        }


        public static class CustomExamineBean {

            public int id;
            public int enterId;
            public int cate;
            public int classify;
            public String classifyName;
            public String person;
        }

        public static class UserOrgListBean {

            public String appoName;
            public int id;
            public int isDefault;
            public int isManager;
            public int orgLevel;
            public int organId;
            public String organName;
            public int userId;
        }

        public static class SubEnterpriseListBean {

            public String name;
            public int id;

        }

    }
}
