package com.power.common.entity;

import java.io.Serializable;
import java.util.Date;

public class SysUser implements Serializable {
	private static final long serialVersionUID = 6321614169730791627L;

	private String userid;

    private String organizeid;

    private String departmentid;

    private String account;

    private String password;

    private String secretkey;

    private String realname;

    private String nickname;

    private String headicon;

    private String quickquery;

    private String simplespelling;

    private String gender;

    private Date birthday;

    private String mobile;

    private String telephone;

    private String email;

    private String oicq;

    private String wechat;

    private String managerid;

    private String manager;

    private String roleid;

    private String dutyid;

    private String dutyname;

    private String postid;

    private String postname;

    private String workgroupid;

    private String modifyuserid;

    private Integer useronline;

    private String openid;

    private String question;

    private String answerquestion;

    private Integer checkonline;

    private Date allowstarttime;

    private Date allowendtime;

    private Date lockstartdate;

    private Date lockenddate;

    private Date firstvisit;

    private Date previousvisit;

    private Integer logoncount;

    private String createusername;

    private Integer securitylevel;

    private String modifyusername;

    private Integer sortcode;

    private Integer deletemark;

    private Integer enabledmark;

    private String description;

    private Date createdate;

    private String createuserid;

    private Date modifydate;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid == null ? null : userid.trim();
    }

    public String getOrganizeid() {
        return organizeid;
    }

    public void setOrganizeid(String organizeid) {
        this.organizeid = organizeid == null ? null : organizeid.trim();
    }

    public String getDepartmentid() {
        return departmentid;
    }

    public void setDepartmentid(String departmentid) {
        this.departmentid = departmentid == null ? null : departmentid.trim();
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account == null ? null : account.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getSecretkey() {
        return secretkey;
    }

    public void setSecretkey(String secretkey) {
        this.secretkey = secretkey == null ? null : secretkey.trim();
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname == null ? null : realname.trim();
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname == null ? null : nickname.trim();
    }

    public String getHeadicon() {
        return headicon;
    }

    public void setHeadicon(String headicon) {
        this.headicon = headicon == null ? null : headicon.trim();
    }

    public String getQuickquery() {
        return quickquery;
    }

    public void setQuickquery(String quickquery) {
        this.quickquery = quickquery == null ? null : quickquery.trim();
    }

    public String getSimplespelling() {
        return simplespelling;
    }

    public void setSimplespelling(String simplespelling) {
        this.simplespelling = simplespelling == null ? null : simplespelling.trim();
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender == null ? null : gender.trim();
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone == null ? null : telephone.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getOicq() {
        return oicq;
    }

    public void setOicq(String oicq) {
        this.oicq = oicq == null ? null : oicq.trim();
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat == null ? null : wechat.trim();
    }

    public String getManagerid() {
        return managerid;
    }

    public void setManagerid(String managerid) {
        this.managerid = managerid == null ? null : managerid.trim();
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager == null ? null : manager.trim();
    }

    public String getRoleid() {
        return roleid;
    }

    public void setRoleid(String roleid) {
        this.roleid = roleid == null ? null : roleid.trim();
    }

    public String getDutyid() {
        return dutyid;
    }

    public void setDutyid(String dutyid) {
        this.dutyid = dutyid == null ? null : dutyid.trim();
    }

    public String getDutyname() {
        return dutyname;
    }

    public void setDutyname(String dutyname) {
        this.dutyname = dutyname == null ? null : dutyname.trim();
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid == null ? null : postid.trim();
    }

    public String getPostname() {
        return postname;
    }

    public void setPostname(String postname) {
        this.postname = postname == null ? null : postname.trim();
    }

    public String getWorkgroupid() {
        return workgroupid;
    }

    public void setWorkgroupid(String workgroupid) {
        this.workgroupid = workgroupid == null ? null : workgroupid.trim();
    }

    public String getModifyuserid() {
        return modifyuserid;
    }

    public void setModifyuserid(String modifyuserid) {
        this.modifyuserid = modifyuserid == null ? null : modifyuserid.trim();
    }

    public Integer getUseronline() {
        return useronline;
    }

    public void setUseronline(Integer useronline) {
        this.useronline = useronline;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid == null ? null : openid.trim();
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question == null ? null : question.trim();
    }

    public String getAnswerquestion() {
        return answerquestion;
    }

    public void setAnswerquestion(String answerquestion) {
        this.answerquestion = answerquestion == null ? null : answerquestion.trim();
    }

    public Integer getCheckonline() {
        return checkonline;
    }

    public void setCheckonline(Integer checkonline) {
        this.checkonline = checkonline;
    }

    public Date getAllowstarttime() {
        return allowstarttime;
    }

    public void setAllowstarttime(Date allowstarttime) {
        this.allowstarttime = allowstarttime;
    }

    public Date getAllowendtime() {
        return allowendtime;
    }

    public void setAllowendtime(Date allowendtime) {
        this.allowendtime = allowendtime;
    }

    public Date getLockstartdate() {
        return lockstartdate;
    }

    public void setLockstartdate(Date lockstartdate) {
        this.lockstartdate = lockstartdate;
    }

    public Date getLockenddate() {
        return lockenddate;
    }

    public void setLockenddate(Date lockenddate) {
        this.lockenddate = lockenddate;
    }

    public Date getFirstvisit() {
        return firstvisit;
    }

    public void setFirstvisit(Date firstvisit) {
        this.firstvisit = firstvisit;
    }

    public Date getPreviousvisit() {
        return previousvisit;
    }

    public void setPreviousvisit(Date previousvisit) {
        this.previousvisit = previousvisit;
    }

    public Integer getLogoncount() {
        return logoncount;
    }

    public void setLogoncount(Integer logoncount) {
        this.logoncount = logoncount;
    }

    public String getCreateusername() {
        return createusername;
    }

    public void setCreateusername(String createusername) {
        this.createusername = createusername == null ? null : createusername.trim();
    }

    public Integer getSecuritylevel() {
        return securitylevel;
    }

    public void setSecuritylevel(Integer securitylevel) {
        this.securitylevel = securitylevel;
    }

    public String getModifyusername() {
        return modifyusername;
    }

    public void setModifyusername(String modifyusername) {
        this.modifyusername = modifyusername == null ? null : modifyusername.trim();
    }

    public Integer getSortcode() {
        return sortcode;
    }

    public void setSortcode(Integer sortcode) {
        this.sortcode = sortcode;
    }

    public Integer getDeletemark() {
        return deletemark;
    }

    public void setDeletemark(Integer deletemark) {
        this.deletemark = deletemark;
    }

    public Integer getEnabledmark() {
        return enabledmark;
    }

    public void setEnabledmark(Integer enabledmark) {
        this.enabledmark = enabledmark;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public Date getCreatedate() {
        return createdate;
    }

    public void setCreatedate(Date createdate) {
        this.createdate = createdate;
    }

    public String getCreateuserid() {
        return createuserid;
    }

    public void setCreateuserid(String createuserid) {
        this.createuserid = createuserid == null ? null : createuserid.trim();
    }

    public Date getModifydate() {
        return modifydate;
    }

    public void setModifydate(Date modifydate) {
        this.modifydate = modifydate;
    }
}