package ddsys;

import java.util.Collections;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

/**
 * MySQL 代码生成
 * 
 * @author: calos
 * @version: 1.0.0
 * @createTime: 2023/11/3 16:53
 **/
public class MySQLGeneratorTest {
    /**
     * 文件路径
     */
    public static final String path = "D:\\IDEAProjects\\sangeng-ddsys-parent\\service\\service-sys\\src\\main\\";

    /**
     * 数据源配置
     */
    public static final DataSourceConfig DATA_SOURCE_CONFIG =
        new DataSourceConfig.Builder("jdbc:mysql://127.0.0.1:3306/shequ-sys?characterEncoding=utf-8&useSSL=false",
            "root", "123456").build();

    /**
     * 全局配置(GlobalConfig)
     */
    public static final GlobalConfig GLOBAL_CONFIG = new GlobalConfig.Builder() // 全局配置
        .outputDir(path + "java") // 输出路径
        .author("calos") // 作者
        .disableOpenDir() // 禁止打开输出目录 默认值:true
        .enableSwagger() // 开启 swagger 模式
        .dateType(DateType.TIME_PACK) // 时间策略 DateType.ONLY_DATE 默认值: DateType.TIME_PACK
        .commentDate("yyyy-MM-dd") // 注释日期 默认值: yyyy-MM-dd
        .build();

    /**
     * 包配置
     */
    public static final PackageConfig PACKAGE_CONFIG = new PackageConfig.Builder() // 包配置
        .parent("com.sangeng.ddsys") // 父包名 默认值:com.baomidou
        .moduleName("sys") // 父包模块名 默认值:无
        .entity("model") // Entity 包名 默认值:entity
        .service("service") // Service 包名 默认值:service
        .serviceImpl("service.impl") // Service Impl 包名 默认值:service.impl
        .mapper("mapper") // Mapper 包名 默认值:mapper
        .xml("mapper.xml") // Mapper XML 包名 默认值:mapper.xml
        .controller("controller") // Controller 包名 默认值:controller
        .pathInfo(Collections.singletonMap(OutputFile.xml, path + "resources\\mapper")) // 设置mapperXML生成路径
        .build();

    /**
     * 策略配置
     */
    public static final StrategyConfig STRATEGY_CONFIG = new StrategyConfig.Builder() // 策略配置
        // .enableCapitalMode() // 开启大写命名 默认值:false
        // .enableSkipView() // 开启跳过视图 默认值:false
        // .disableSqlFilter() // 禁用 sql 过滤 默认值:true，语法不能支持使用 sql 过滤表的话，可以考虑关闭此开关
        // .likeTable(new LikeTable("USER")) // likeTable(LikeTable) 模糊表匹配(sql 过滤) likeTable 与 notLikeTable 只能配置一项
        .addInclude("region", "ware", "region_ware") // 增加表匹配(内存过滤)
        // .addTablePrefix("t_", "c_") // 增加过滤表前缀
        // .addFieldSuffix("_flag") // 增加过滤表后缀
        .entityBuilder() // Entity 策略配置
        .enableFileOverride() // 覆盖已有文件
        .enableLombok() // 开启 lombok 模型
        // .logicDeleteColumnName("is_deleted") // 逻辑删除字段名(数据库字段)
        .naming(NamingStrategy.underline_to_camel) // 数据库表映射到实体的命名策略,默认下划线转驼峰命名:NamingStrategy.underline_to_camel
        .columnNaming(NamingStrategy.underline_to_camel) // 数据库表字段映射到实体的命名策略 默认为 null，未指定按照 naming 执行
        .controllerBuilder() // Controller 策略配置
        .enableFileOverride() // 覆盖已有文件
        .enableRestStyle() // 开启生成@RestController 控制器 默认值:false
        .enableHyphenStyle() // 开启驼峰转连字符 默认值:false
        .serviceBuilder() // Service 策略配置
        .formatServiceFileName("%sService") // 格式化 service 接口文件名称，去掉Service接口的首字母I
        .formatServiceImplFileName("%sServiceImp") // 格式化 service 接口文件名称，去掉Service接口的首字母I
        .build();

    public static void main(String[] args) {
        AutoGenerator generator = new AutoGenerator(DATA_SOURCE_CONFIG);
        generator.strategy(STRATEGY_CONFIG);
        generator.global(GLOBAL_CONFIG);
        generator.packageInfo(PACKAGE_CONFIG);
        generator.execute();
    }
}
