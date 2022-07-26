package au.com.roaringventures.api.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/test")
public class TestAdminApi {

    @GetMapping()
    public Map<String, Object> test() {
        Map<String, Object> map = new HashMap<>();
        map.put("test", "Admin Request");
        return map;
    }

}
