package com.doublechain.id.starter.database;

import com.doublechain.id.IdSpace;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by gaopeng on 2018/3/30.
 */
@Service
@Slf4j
public class IdSpaceService {

    @Autowired
    private IdSpaceDAO repository;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public IdSpace nextBatch(String spaceName) {
        IdSpaceDO idSpace = repository.findIdSpace(spaceName);
        Long seed = idSpace.getSeed();
        Integer steps = idSpace.getSteps();
        Integer length = idSpace.getSeqLength();
        IdSpaceDO newIdSpace = new IdSpaceDO();
        newIdSpace.setId(idSpace.getId());
        newIdSpace.setSeed(seed + steps.longValue());
        int seedLength = newIdSpace.getSeed().toString().length();
        if (seedLength > length) {
            newIdSpace.setSeqLength(seedLength);
        } else {
            newIdSpace.setSeqLength(length);
        }
        repository.updateIdSpace(newIdSpace);
        IdSpace result = new IdSpace();
        result.setFrom(idSpace.getSeed());
        result.setSpaceName(idSpace.getSpaceName());
        result.setPrefix(idSpace.getPrefix());
        result.setTo(idSpace.getSeed() + idSpace.getSteps());
        result.setSeqLength(length);
        result.setReplenishThreshold(idSpace.getReplenishThreshold());
        result.setSuffix(idSpace.getSuffix());
        log.info("获取新批次id space={}", result);
        return result;
    }
}
