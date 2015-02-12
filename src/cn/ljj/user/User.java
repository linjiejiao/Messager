package cn.ljj.user;

public class User {
	private String mName;
	private String mIdentity;
	private String mPassword;

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	public String getIdentity() {
		return mIdentity;
	}

	public void setIdentity(String identity) {
		mIdentity = identity;
	}

	public String getPassword() {
		return mPassword;
	}

	public void setmPassword(String password) {
		mPassword = password;
	}

	@Override
	public String toString() {
		return "User [mName=" + mName + ", mIdentity=" + mIdentity
				+ ", mPassword=" + mPassword + "]";
	}
}
