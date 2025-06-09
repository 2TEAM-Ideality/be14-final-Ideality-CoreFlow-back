package com.ideality.coreflow.auth.command.domain.aggregate;

public enum LoginType {
    EMAIL,
    EMPLOYEE_NUM;

    public static LoginType fromIdentifier(String identifier) {
        if (identifier != null && identifier.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            return EMAIL;
        } else {
            return EMPLOYEE_NUM;
        }
    }
}
