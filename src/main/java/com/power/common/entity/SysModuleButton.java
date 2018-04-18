package com.power.common.entity;

public class SysModuleButton {
    private String buttonid;

    private String moduleid;

    private String parentid;

    private String icon;

    private String code;

    private String fullname;

    private String actionaddress;

    private Integer sortcode;
    private Integer checked;
    public Integer getChecked() {
		return checked;
	}

	public void setChecked(Integer checked) {
		this.checked = checked;
	}

	public String getButtonid() {
        return buttonid;
    }

    public void setButtonid(String buttonid) {
        this.buttonid = buttonid == null ? null : buttonid.trim();
    }

    public String getModuleid() {
        return moduleid;
    }

    public void setModuleid(String moduleid) {
        this.moduleid = moduleid == null ? null : moduleid.trim();
    }

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid == null ? null : parentid.trim();
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon == null ? null : icon.trim();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname == null ? null : fullname.trim();
    }

    public String getActionaddress() {
        return actionaddress;
    }

    public void setActionaddress(String actionaddress) {
        this.actionaddress = actionaddress == null ? null : actionaddress.trim();
    }

    public Integer getSortcode() {
        return sortcode;
    }

    public void setSortcode(Integer sortcode) {
        this.sortcode = sortcode;
    }
}