package com.doublechain.id.starter.database;

import com.doublechain.id.IdSpace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by gaopeng on 2018/3/30.
 */
@RestController
@RequestMapping("/id")
public class IdSpaceController {
    @Autowired
    private IdSpaceService idSpaceService;

    @GetMapping
    public IdSpace fetchIdSpace(@RequestParam String spaceName) {
        IdSpace idSpace = idSpaceService.nextBatch(spaceName);
        return idSpace;
    }
}
