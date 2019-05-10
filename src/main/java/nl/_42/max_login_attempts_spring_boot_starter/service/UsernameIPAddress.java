package nl._42.max_login_attempts_spring_boot_starter.service;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Tuple class that holds a combination of username and IP-address,
 * which is the combined key to determine if a user account is
 * temporarily blocked.
 */
class UsernameIPAddress implements Comparable<UsernameIPAddress> {

    private final String username;
    private final String ipAddress;

    UsernameIPAddress(String username, String ipAddress) {
        this.username = username;
        this.ipAddress = ipAddress;
    }

    String getUsername() {
        return username;
    }

    String getIpAddress() {
        return ipAddress;
    }

    @Override
    public int compareTo(UsernameIPAddress o) {
        return new CompareToBuilder()
                .append(username, o.username)
                .append(ipAddress, o.ipAddress)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        UsernameIPAddress other = (UsernameIPAddress) o;

        return new EqualsBuilder()
                .append(username, other.username)
                .append(ipAddress, other.ipAddress)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(username)
                .append(ipAddress)
                .build();
    }
}
