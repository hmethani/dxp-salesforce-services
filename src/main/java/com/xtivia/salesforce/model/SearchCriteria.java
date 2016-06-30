package com.xtivia.salesforce.model;

import org.apache.commons.lang3.StringUtils;

public class SearchCriteria {

    private final String name;
    private final String email;
    private final String company;

    public static final SearchCriteria EMPTY_CRITERIA = new SearchCriteria("", "", "");

    public SearchCriteria(String name, String email, String company) {
        this.name = name;
        this.email = email;
        this.company = company;
    }

    public String getName() {
        return StringUtils.isEmpty(name) ? "%" : "%" + name + "%";
    }

    public String getEmail() {
        return StringUtils.isEmpty(email) ? "%" : "%" + email + "%";
    }

    public String getCompany() {
        return StringUtils.isEmpty(company) ? "%" : "%" + company + "%";
    }

    @Override
    public String toString() {
        return "SearchCriteria [name=" + getName() + ", email=" + getEmail() + ", company=" + getCompany() + "]";
    }

    public String searchQuery() {
        return String.format("Select Id, Name, City, Company, Email, Status " 
                + "From Lead "
                + "Where Name LIKE'%s' and Email LIKE '%s' and Company LIKE '%s' "
                + "LIMIT 200", getName(), getEmail(), getCompany());
    }

}
