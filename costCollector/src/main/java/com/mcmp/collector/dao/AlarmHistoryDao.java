package com.mcmp.collector.dao;

import com.mcmp.collector.dto.AlarmHistoryDto;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

/**
 * Alarm History DAO
 */
@Repository
public class AlarmHistoryDao {

    @Autowired
    @Qualifier("sqlSessionTemplateSimple")
    private SqlSessionTemplate sqlSessionTemplate;

    /**
     * 알람 이력 저장
     * @param alarmHistoryDto 알람 이력 정보
     * @return 저장된 행 수
     */
    public int insertAlarmHistory(AlarmHistoryDto alarmHistoryDto) {
        return sqlSessionTemplate.insert("alarmHistory.insertAlarmHistory", alarmHistoryDto);
    }
}
