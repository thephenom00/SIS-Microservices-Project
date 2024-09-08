package cz.cvut.fel.ear.sis.security.model;

import cz.cvut.fel.ear.sis.model.Person;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class CustomUserDetails implements org.springframework.security.core.userdetails.UserDetails {

    private Person person;
    private static final long serialVersionUID = 1L;

    private Set<GrantedAuthority> authorities = null;

    public CustomUserDetails(Person user) {
        Objects.requireNonNull(user);
        this.person = user;
        this.authorities = new HashSet<>();
        addUserRole();
    }

    public CustomUserDetails(Person person, Collection<GrantedAuthority> authorities) {
        Objects.requireNonNull(person);
        Objects.requireNonNull(authorities);
        this.person = person;
        this.authorities = new HashSet<>();
        addUserRole();
        this.authorities.addAll(authorities);
    }

    private void addUserRole() {
        authorities.add(new SimpleGrantedAuthority(person.getRole()));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.unmodifiableCollection(authorities);
    }

    @Override
    public String getPassword() {
        return person.getPassword();
    }

    @Override
    public String getUsername() {
        return person.getUserName();
    }


    public Long getId() {
        return person.getId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Person person() {
        return person;
    }

    public void setPerson(Person person){
        this.person = person;
    }

    public void setAuthorities(Set<GrantedAuthority> grantedAuthorities)
    {
        this.authorities = grantedAuthorities;
    }
}
