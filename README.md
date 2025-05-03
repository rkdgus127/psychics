### Psychics Final Class

---

* #### Features
    * 능력자의 코드 개선

기존 능력자 (제가 만든 능력자) 코드에는 매우 불편한 점이 존재합니다.

```java
//위의 커맨드 코드
case "Sample":
  //셈플의 능력 아이템 지급 혹은 메시지
//셈플의 능력
//셈플의 tabcompletements
```

이런식으로 매우 복잡하게 코드를 작성해야합니다.

### 하지만
*Psychics*는 이와 같은 문제점을 해결할 수 있도록 새로운 상속 클래스를 제공합니다.

---
아까와 같은 코드가 있다고 생각해 봅시다

이번엔 Psychics를 사용하여 작성한 코드입니다

```java
//기존에는 메인클래스에서 다 했지만
//현재는 새로운 클래스에서 능력 개발
public class Sample extends Ability {
  public static class Info extends AbilityInfo {
    //설명
  }
//능력 구현 로직
}
```

이런식으로 자동으로 리스너 클래스가 등록되고 Tab과 명령어 지원까지 되기때문에
능력 개발만 하면 됩니다.

---

---
