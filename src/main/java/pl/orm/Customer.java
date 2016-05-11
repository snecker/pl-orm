package pl.orm;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Customer extends Base {
    private Integer customerId;
    private String csCustomerId;
    private String code;
    private String kind;
    private String contacts;
    private String mobile;
    private String email;
    private String address;
    private String title;
    private String agentCode;

    public String getAgentCode() {
        return agentCode;
    }

    public void setAgentCode(String agentCode) {
        this.agentCode = agentCode;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCsCustomerId() {
        return csCustomerId;
    }

    public void setCsCustomerId(String csCustomerId) {
        this.csCustomerId = csCustomerId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Customer customer = (Customer) o;

        return !(getCsCustomerId() != null ? !getCsCustomerId().equals(customer.getCsCustomerId()) : customer.getCsCustomerId() != null);

    }

    @Override
    public int hashCode() {
        return getCsCustomerId() != null ? getCsCustomerId().hashCode() : 0;
    }

    /**
     * 返回true则是客户，false是门店。
     *
     * @return
     */
    public boolean isCustomer() {
        return code == null ? true : code.indexOf('@') == -1;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
