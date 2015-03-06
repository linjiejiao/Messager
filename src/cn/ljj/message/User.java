
package cn.ljj.message;


public class User {
    private String mName = null;
    private String mPassword = null;
    private int mIdentity = 0;
    private int mStatus = 0;

    public static final int STATUS_OFF_LINE = 0;
    public static final int STATUS_ON_LINE = 1;

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
    public boolean equals(Object o) {
        if(o instanceof User){
            User other = (User)o;
            if(mIdentity == other.mIdentity && mPassword.equals(other.mPassword)
                    && mName.equals(other.mName)){
                return true;
            }
        }
        return super.equals(o);
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
