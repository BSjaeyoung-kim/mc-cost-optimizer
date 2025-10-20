package com.mcmp.assetcollector.contorller;

import com.mcmp.assetcollector.dto.MetricDataDto;
import com.mcmp.assetcollector.dto.MonitoringResponseDto;
import com.mcmp.assetcollector.dto.TagDto;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MonitoringSampleData {

    // sample/mem.json 기반으로 만드는 샘플 데이터.
    public static MonitoringResponseDto createMemSampleData() {
        MonitoringResponseDto response = new MonitoringResponseDto();

        // MetricData 생성
        MetricDataDto metricData = new MetricDataDto();
        metricData.setName("mem");
        metricData.setColumns(Arrays.asList("timestamp", "used_percent"));

        // Tags 생성
        TagDto tags = new TagDto();
        tags.setNsId("ns01");
        tags.setMciId("mci01");
        tags.setVmId("16880170");
        metricData.setTags(tags);

        // Values 생성
        List<List<Object>> values = new ArrayList<>();
        values.add(Arrays.asList("2025-10-14T06:45:00Z", 9.5597701917429));
        values.add(Arrays.asList("2025-10-14T06:44:00Z", 9.5597701917429));
        values.add(Arrays.asList("2025-10-14T06:43:00Z", 9.798663474109674));
        values.add(Arrays.asList("2025-10-14T06:42:00Z", 9.765803805339123));
        values.add(Arrays.asList("2025-10-14T06:41:00Z", 9.589488496124154));
        values.add(Arrays.asList("2025-10-14T06:40:00Z", 9.89948631212796));
        values.add(Arrays.asList("2025-10-14T06:39:00Z", 9.466925422014882));
        values.add(Arrays.asList("2025-10-14T06:38:00Z", 9.52371929565624));
        values.add(Arrays.asList("2025-10-14T06:37:00Z", 9.703425283894575));
        values.add(Arrays.asList("2025-10-14T06:36:00Z", 9.88567428140043));
        values.add(Arrays.asList("2025-10-14T06:35:00Z", 9.799311692158255));
        values.add(Arrays.asList("2025-10-14T06:34:00Z", 9.68741928438722));
        values.add(Arrays.asList("2025-10-14T06:33:00Z", 9.641296077084096));
        values.add(Arrays.asList("2025-10-14T06:32:00Z", 9.650770033178791));
        values.add(Arrays.asList("2025-10-14T06:31:00Z", 9.45999447518771));
        values.add(Arrays.asList("2025-10-14T06:30:00Z", 9.756529300951684));
        values.add(Arrays.asList("2025-10-14T06:29:00Z", 9.641894432205866));
        values.add(Arrays.asList("2025-10-14T06:28:00Z", 9.688665857557574));
        values.add(Arrays.asList("2025-10-14T06:27:00Z", 9.68218367707173));
        values.add(Arrays.asList("2025-10-14T06:26:00Z", 9.621251180504792));
        values.add(Arrays.asList("2025-10-14T06:25:00Z", 9.674056020001018));
        values.add(Arrays.asList("2025-10-14T06:24:00Z", 9.462088718113904));
        values.add(Arrays.asList("2025-10-14T06:23:00Z", 9.709957327307233));
        values.add(Arrays.asList("2025-10-14T06:22:00Z", 9.862488020431833));
        values.add(Arrays.asList("2025-10-14T06:21:00Z", 9.587194801490703));
        values.add(Arrays.asList("2025-10-14T06:20:00Z", 9.569692914178923));
        values.add(Arrays.asList("2025-10-14T06:19:00Z", 9.765355038997795));
        values.add(Arrays.asList("2025-10-14T06:18:00Z", 9.650770033178791));
        values.add(Arrays.asList("2025-10-14T06:17:00Z", 9.733243314129458));
        values.add(Arrays.asList("2025-10-14T06:16:00Z", 9.8497231111674));
        values.add(Arrays.asList("2025-10-14T06:15:00Z", 9.781710078992848));
        values.add(Arrays.asList("2025-10-14T06:14:00Z", 9.711503078038472));
        values.add(Arrays.asList("2025-10-14T06:13:00Z", 9.810680439471891));
        values.add(Arrays.asList("2025-10-14T06:12:00Z", 9.911802455051065));
        values.add(Arrays.asList("2025-10-14T06:11:00Z", 9.624043504406387));
        values.add(Arrays.asList("2025-10-14T06:10:00Z", 9.47001692347736));
        values.add(Arrays.asList("2025-10-14T06:09:00Z", 9.73149811169096));
        values.add(Arrays.asList("2025-10-14T06:08:00Z", 9.91983038626815));
        values.add(Arrays.asList("2025-10-14T06:07:00Z", 9.452066269824254));
        values.add(Arrays.asList("2025-10-14T06:06:00Z", 9.825739043369776));
        values.add(Arrays.asList("2025-10-14T06:05:00Z", 11.086273833232443));
        values.add(Arrays.asList("2025-10-14T06:04:00Z", 12.709212375180629));
        values.add(Arrays.asList("2025-10-14T06:03:00Z", 11.30532167072717));
        values.add(Arrays.asList("2025-10-14T06:02:00Z", 9.601306009779117));
        values.add(Arrays.asList("2025-10-14T06:01:00Z", 9.601156420998674));
        values.add(Arrays.asList("2025-10-14T06:00:00Z", 9.66243795805331));
        values.add(Arrays.asList("2025-10-14T05:59:00Z", 9.577720845396005));
        values.add(Arrays.asList("2025-10-14T05:58:00Z", 9.612575031239125));
        values.add(Arrays.asList("2025-10-14T05:57:00Z", 9.474753901524709));
        values.add(Arrays.asList("2025-10-14T05:56:00Z", 9.597067660999912));
        values.add(Arrays.asList("2025-10-14T05:55:00Z", 9.51274945175712));
        values.add(Arrays.asList("2025-10-14T05:54:00Z", 9.560418409791483));
        values.add(Arrays.asList("2025-10-14T05:53:00Z", 9.651268662446933));
        values.add(Arrays.asList("2025-10-14T05:52:00Z", 9.55882279613343));
        values.add(Arrays.asList("2025-10-14T05:51:00Z", 9.54750391174661));
        values.add(Arrays.asList("2025-10-14T05:50:00Z", 9.71040609364856));
        values.add(Arrays.asList("2025-10-14T05:49:00Z", 9.725065794131933));
        values.add(Arrays.asList("2025-10-14T05:48:00Z", 9.61063037709337));
        values.add(Arrays.asList("2025-10-14T05:47:00Z", 9.485823471277458));
        values.add(Arrays.asList("2025-10-14T05:46:00Z", 9.625040762942671));
        values.add(Arrays.asList("2025-10-14T05:45:00Z", 9.625040762942671));

        metricData.setValues(values);

        response.setData(List.of(metricData));

        return response;
    }

    // sample/cpu.json 기반으로 만드는 샘플 데이터.
    public static MonitoringResponseDto createCpuSampleData() {
        MonitoringResponseDto response = new MonitoringResponseDto();

        // MetricData 생성
        MetricDataDto metricData = new MetricDataDto();
        metricData.setName("cpu");
        metricData.setColumns(Arrays.asList("timestamp", "usage_idle"));

        // Tags 생성
        TagDto tags = new TagDto();
        tags.setNsId("ns01");
        tags.setMciId("mci01");
        tags.setVmId("16880170");
        metricData.setTags(tags);

        // Values 생성
        List<List<Object>> values = new ArrayList<>();
        values.add(Arrays.asList("2025-10-14T06:46:00Z", 91.4659360183491));
        values.add(Arrays.asList("2025-10-14T06:45:00Z", 91.4659360183491));
        values.add(Arrays.asList("2025-10-14T06:44:00Z", 91.64363636118168));
        values.add(Arrays.asList("2025-10-14T06:43:00Z", 91.80951774359694));
        values.add(Arrays.asList("2025-10-14T06:42:00Z", 91.51109101994246));
        values.add(Arrays.asList("2025-10-14T06:41:00Z", 91.57108814236645));
        values.add(Arrays.asList("2025-10-14T06:40:00Z", 91.62827387230112));
        values.add(Arrays.asList("2025-10-14T06:39:00Z", 91.60809906291854));
        values.add(Arrays.asList("2025-10-14T06:38:00Z", 91.4811152880747));
        values.add(Arrays.asList("2025-10-14T06:37:00Z", 92.0418410041763));
        values.add(Arrays.asList("2025-10-14T06:36:00Z", 91.63803514174718));
        values.add(Arrays.asList("2025-10-14T06:35:00Z", 91.4235587835525));
        values.add(Arrays.asList("2025-10-14T06:34:00Z", 91.72309529427582));
        values.add(Arrays.asList("2025-10-14T06:33:00Z", 91.53205102317958));
        values.add(Arrays.asList("2025-10-14T06:32:00Z", 91.43573099489423));
        values.add(Arrays.asList("2025-10-14T06:31:00Z", 91.58933479026894));
        values.add(Arrays.asList("2025-10-14T06:30:00Z", 91.53973400071717));
        values.add(Arrays.asList("2025-10-14T06:29:00Z", 91.67712318430911));
        values.add(Arrays.asList("2025-10-14T06:28:00Z", 91.65411413288643));
        values.add(Arrays.asList("2025-10-14T06:27:00Z", 91.69106925280957));
        values.add(Arrays.asList("2025-10-14T06:26:00Z", 91.65829986612951));
        values.add(Arrays.asList("2025-10-14T06:25:00Z", 91.68483276454886));
        values.add(Arrays.asList("2025-10-14T06:24:00Z", 91.66806626528954));
        values.add(Arrays.asList("2025-10-14T06:23:00Z", 91.81659413105862));
        values.add(Arrays.asList("2025-10-14T06:22:00Z", 91.56072264971837));
        values.add(Arrays.asList("2025-10-14T06:21:00Z", 91.7433730685004));
        values.add(Arrays.asList("2025-10-14T06:20:00Z", 91.63026856250724));
        values.add(Arrays.asList("2025-10-14T06:19:00Z", 91.83825377710585));
        values.add(Arrays.asList("2025-10-14T06:18:00Z", 91.69245298631954));
        values.add(Arrays.asList("2025-10-14T06:17:00Z", 91.68136214706219));
        values.add(Arrays.asList("2025-10-14T06:16:00Z", 91.77475732778831));
        values.add(Arrays.asList("2025-10-14T06:15:00Z", 92.0689576105909));
        values.add(Arrays.asList("2025-10-14T06:14:00Z", 91.95882833986909));
        values.add(Arrays.asList("2025-10-14T06:13:00Z", 91.79701836429493));
        values.add(Arrays.asList("2025-10-14T06:12:00Z", 91.63882239954926));
        values.add(Arrays.asList("2025-10-14T06:11:00Z", 91.63039839303717));
        values.add(Arrays.asList("2025-10-14T06:10:00Z", 91.80544539479554));
        values.add(Arrays.asList("2025-10-14T06:09:00Z", 91.66458278762275));
        values.add(Arrays.asList("2025-10-14T06:08:00Z", 91.58016093340805));
        values.add(Arrays.asList("2025-10-14T06:07:00Z", 91.27051508637292));
        values.add(Arrays.asList("2025-10-14T06:06:00Z", 91.3006955457094));
        values.add(Arrays.asList("2025-10-14T06:05:00Z", 59.70522794051726));
        values.add(Arrays.asList("2025-10-14T06:04:00Z", 38.73452790319529));
        values.add(Arrays.asList("2025-10-14T06:03:00Z", 71.12381245455376));
        values.add(Arrays.asList("2025-10-14T06:02:00Z", 91.42046053575207));
        values.add(Arrays.asList("2025-10-14T06:01:00Z", 91.26824499064814));
        values.add(Arrays.asList("2025-10-14T06:00:00Z", 91.46637278392015));
        values.add(Arrays.asList("2025-10-14T05:59:00Z", 91.57320331431156));
        values.add(Arrays.asList("2025-10-14T05:58:00Z", 91.19736547305924));
        values.add(Arrays.asList("2025-10-14T05:57:00Z", 91.13359388728398));
        values.add(Arrays.asList("2025-10-14T05:56:00Z", 91.47133858048525));
        values.add(Arrays.asList("2025-10-14T05:55:00Z", 91.3640167363979));
        values.add(Arrays.asList("2025-10-14T05:54:00Z", 91.23164245912216));
        values.add(Arrays.asList("2025-10-14T05:53:00Z", 91.38145468905958));
        values.add(Arrays.asList("2025-10-14T05:52:00Z", 91.83259093733824));
        values.add(Arrays.asList("2025-10-14T05:51:00Z", 91.97485248226081));
        values.add(Arrays.asList("2025-10-14T05:50:00Z", 91.82784889187292));
        values.add(Arrays.asList("2025-10-14T05:49:00Z", 92.00703046536034));
        values.add(Arrays.asList("2025-10-14T05:48:00Z", 91.94981577913228));
        values.add(Arrays.asList("2025-10-14T05:47:00Z", 91.97219543227602));
        values.add(Arrays.asList("2025-10-14T05:46:00Z", 91.97219543227602));

        metricData.setValues(values);

        response.setData(Arrays.asList(metricData));

        return response;
    }
}
