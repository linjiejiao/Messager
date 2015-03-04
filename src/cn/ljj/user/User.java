
package cn.ljj.user;

import cn.ljj.message.Headers;

public class User {
    private String mName;
    private String mPassword;
    private int mIdentity;
    private int mStatus;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getIdentity() {
        return mIdentity;
    }

    public void setIdentity(int identity) {
        mIdentity = identity;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        mStatus = status;
    }

    @Override
    public String toString() {
        return "User [mName=" + mName + ", mPassword=" + mPassword + ", mIdentity=" + mIdentity
                + ", mStatus=" + mStatus + "]";
    }

    public static final int[] INT_HEADERS = new int[] {
            Headers.HEADER_USER_IDENTITY, Headers.HEADER_USER_STATUS
    };

    public static final int[] STRING_HEADERS = new int[] {
            Headers.HEADER_USER_NAME, Headers.HEADER_USER_PASSWORD
    };
}
