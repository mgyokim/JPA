package hellojpa;

import javax.persistence.*;
import javax.sound.sampled.FloatControl;
import java.time.LocalDateTime;

@Entity
public class Member {
    @Id
    @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column(name = "USERNAME")
    private String username;

    // 기간 Period
    @Embedded
    private Period workPreiod;

    // 주소
    @Embedded
    private Adress homeAddress;

    // 주소
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="city",
                column = @Column(name = "WORK_CITY")),
            @AttributeOverride(name="street",
                    column = @Column(name = "WORK_STREET")),
            @AttributeOverride(name="zipcode",
                    column = @Column(name = "WORK_ZIPCODE"))
    })
    private Adress workAddress;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Period getWorkPreiod() {
        return workPreiod;
    }

    public void setWorkPreiod(Period workPreiod) {
        this.workPreiod = workPreiod;
    }

    public Adress getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(Adress homeAddress) {
        this.homeAddress = homeAddress;
    }
}