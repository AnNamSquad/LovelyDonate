package org.lovelysmp.lovelypay.api;

public interface DatabaseSettings {

    public String getType();

    public String getHost();

    public int getPort();

    public String getDatabase();

    public String getUsername();

    public String getPassword();
}
