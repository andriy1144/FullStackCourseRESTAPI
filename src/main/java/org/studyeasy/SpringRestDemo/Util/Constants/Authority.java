package org.studyeasy.SpringRestDemo.Util.Constants;

public enum Authority {
    READ,
    WRITE,
    UPDATE,
    USER, // can update delete self object, and read anything
    NYASHNIY_ADMINCHICK,
    ADMIN; // can update delete read everything
}
