package com.doublechain.id.starter.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by gaopeng on 2018/3/30.
 */
@Repository
public class IdSpaceDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int updateIdSpace(IdSpaceDO idSpace) {
        int count = jdbcTemplate.update("UPDATE id_space SET seed=?, seq_length=? WHERE id=?", ps -> {
            ps.setLong(1, idSpace.getSeed());
            ps.setInt(2, idSpace.getSeqLength());
            ps.setInt(3, idSpace.getId());
        });
        return count;
    }

    public IdSpaceDO findIdSpace(String spaceName) {
        IdSpaceDO result = jdbcTemplate.query("SELECT * FROM id_space WHERE space_name=? FOR UPDATE", new Object[]{spaceName}, rs -> {
            if (rs.next()) {
                IdSpaceDO idSpace = new IdSpaceDO();
                idSpace.setId(rs.getInt("id"));
                idSpace.setReplenishThreshold(rs.getInt("replenish_threshold"));
                idSpace.setSuffix(rs.getString("suffix"));
                idSpace.setSeed(rs.getLong("seed"));
                idSpace.setPrefix(rs.getString("prefix"));
                idSpace.setSpaceName(rs.getString("space_name"));
                idSpace.setSteps(rs.getInt("steps"));
                idSpace.setSeqLength(rs.getInt("seq_length"));
                return idSpace;
            }
            return null;
        });
        return result;
    }
}
