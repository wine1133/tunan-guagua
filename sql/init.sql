CREATE DATABASE IF NOT EXISTS guagua_user DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS guagua_content DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS guagua_post DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE guagua_user;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(40) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    nickname VARCHAR(40) NOT NULL,
    avatar VARCHAR(500) DEFAULT '',
    bio VARCHAR(300) DEFAULT '',
    city VARCHAR(60) DEFAULT '兔南',
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS follows (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    follower_id BIGINT NOT NULL,
    following_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_follow (follower_id, following_id),
    KEY idx_following (following_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO users (id, username, password_hash, nickname, avatar, bio, city, role, status) VALUES
(1, 'admin', '$2b$12$GL3oZJsZ1Dh5BccYEqZrfeC6bkubT7hcc7/FpYdCD93x1.bvFmF0m', '兔南管理员', '', '守护每一份同城热爱', '兔南', 'ADMIN', 1),
(2, 'xiaonan', '$2b$12$GL3oZJsZ1Dh5BccYEqZrfeC6bkubT7hcc7/FpYdCD93x1.bvFmF0m', '小南同学', '', '喜欢逛吃、拍照，也喜欢认识新朋友。', '兔南', 'USER', 1),
(3, 'agua', '$2b$12$GL3oZJsZ1Dh5BccYEqZrfeC6bkubT7hcc7/FpYdCD93x1.bvFmF0m', '阿瓜', '', '城市观察员，记录街角的小美好。', '兔南', 'USER', 1),
(4, 'chengzi', '$2b$12$GL3oZJsZ1Dh5BccYEqZrfeC6bkubT7hcc7/FpYdCD93x1.bvFmF0m', '橙子汽水', '', '周末骑行，工作日认真生活。', '兔南', 'USER', 1)
ON DUPLICATE KEY UPDATE nickname = VALUES(nickname);

INSERT IGNORE INTO follows (follower_id, following_id) VALUES (2, 3), (3, 2), (4, 2), (2, 1);

USE guagua_content;

CREATE TABLE IF NOT EXISTS circles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(80) NOT NULL,
    cover VARCHAR(500) DEFAULT '',
    description VARCHAR(500) DEFAULT '',
    audit_status TINYINT NOT NULL DEFAULT 0,
    member_count INT NOT NULL DEFAULT 0,
    post_count INT NOT NULL DEFAULT 0,
    created_by BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS topics (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    cover VARCHAR(500) DEFAULT '',
    description VARCHAR(500) DEFAULT '',
    audit_status TINYINT NOT NULL DEFAULT 0,
    heat INT NOT NULL DEFAULT 0,
    post_count INT NOT NULL DEFAULT 0,
    created_by BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS banners (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    link_url VARCHAR(500) DEFAULT '',
    sort INT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS news (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    summary VARCHAR(500) DEFAULT '',
    content LONGTEXT NOT NULL,
    cover VARCHAR(500) DEFAULT '',
    city VARCHAR(60) DEFAULT '兔南',
    status TINYINT NOT NULL DEFAULT 1,
    view_count INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO circles (id, name, cover, description, audit_status, member_count, post_count, created_by) VALUES
(1, '兔南美食研究所', '/images/jiangnan/zhouzhuang-boat.jpg', '从街边小馆到宝藏甜品店，一起把兔南吃明白。', 1, 328, 12, 2),
(2, '周末去哪儿', '/images/jiangnan/wuzhen-river.jpg', '露营、徒步、展览、骑行，承包你的每个周末。', 1, 216, 8, 3),
(3, '萌宠碰头会', '/images/jiangnan/zhouzhuang-canal.jpg', '分享毛孩子的日常，不定期组织线下遛宠。', 1, 154, 5, 4),
(4, '新晋摄影联盟', '/images/jiangnan/jiangnan-garden.jpg', '用镜头记录城市，每周一个拍摄主题。', 0, 0, 0, 2)
ON DUPLICATE KEY UPDATE description = VALUES(description);

INSERT INTO topics (id, name, cover, description, audit_status, heat, post_count, created_by) VALUES
(1, '兔南的秋天', '/images/jiangnan/xitang-dusk-1.jpg', '晒出你镜头下最有秋日氛围的同城瞬间。', 1, 9821, 16, 2),
(2, '今天吃什么', '/images/jiangnan/tuisi-garden.jpg', '拒绝选择困难，分享你的今日菜单。', 1, 7632, 24, 3),
(3, '城市夜跑搭子', '/images/jiangnan/suzhou-canal.jpg', '安全约跑，互相监督，一起变强。', 1, 4510, 9, 4),
(4, '旧物交换计划', '/images/jiangnan/qibao-canal.jpg', '让闲置找到新主人。', 0, 120, 1, 2)
ON DUPLICATE KEY UPDATE description = VALUES(description);

INSERT INTO banners (id, title, image_url, link_url, sort, status) VALUES
(1, '烟雨江南 · 发现兔南的 100 种生活方式', '/images/jiangnan/qibao-canal-hero.jpg', '/circles', 1, 1),
(2, '一城水巷 · 周末去吹江南的风', '/images/jiangnan/wuzhen-aerial-hero.jpg', '/topics', 2, 1),
(3, '小桥流水 · 把江南故事讲给更多人听', '/images/jiangnan/zhouzhuang-lanterns-hero.jpg', '/news', 3, 1)
ON DUPLICATE KEY UPDATE title = VALUES(title);

INSERT INTO news (id, title, summary, content, cover, city, status, view_count) VALUES
(1, '兔南滨河步道全新开放，夜跑地图请收好', '全长 8.6 公里的滨河步道完成焕新，新增饮水点和夜间照明。', '这个周末，兔南滨河步道正式向市民开放。\n\n此次升级新增了 4 处直饮水点、3 个便民驿站和全线夜间照明。步道分为慢行段、跑步段和亲子段，沿途还设置了多处城市观景平台。\n\n建议大家错峰前往，骑行请减速礼让行人，共同维护舒适的公共空间。', '/images/jiangnan/xitang-dusk-2.jpg', '江南', 1, 2381),
(2, '本周末城市市集指南：咖啡、手作与现场音乐', '两天限定的秋日市集将在老码头街区举行，60 余家摊主参与。', '本周末 10:00-20:00，秋日城市市集将在老码头街区举行。\n\n现场包含精品咖啡、独立手作、旧物交换和本地音乐人演出。活动免费入场，建议乘坐公共交通前往。\n\n参与话题 #兔南的秋天 分享现场照片，还有机会获得市集限定周边。', '/images/jiangnan/wuzhen-river.jpg', '江南', 1, 1642),
(3, '公交 17 路优化调整，串联三个热门生活圈', '自下周一起，17 路新增两处站点，运营时间同步延长。', '为方便市民出行，自下周一起，公交 17 路将进行线路优化。\n\n新增“滨河公园东门”和“创意园北门”两站，末班车时间由 21:30 延长至 22:30。具体站点信息请以公交站牌公告为准。', '/images/jiangnan/suzhou-canal.jpg', '江南', 1, 905)
ON DUPLICATE KEY UPDATE title = VALUES(title);

USE guagua_post;

CREATE TABLE IF NOT EXISTS posts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(160) NOT NULL,
    content LONGTEXT NOT NULL,
    images TEXT,
    circle_id BIGINT DEFAULT NULL,
    topic_id BIGINT DEFAULT NULL,
    author_id BIGINT NOT NULL,
    audit_status TINYINT NOT NULL DEFAULT 0,
    like_count INT NOT NULL DEFAULT 0,
    favorite_count INT NOT NULL DEFAULT 0,
    comment_count INT NOT NULL DEFAULT 0,
    view_count INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_author (author_id),
    KEY idx_circle (circle_id),
    KEY idx_topic (topic_id),
    KEY idx_audit (audit_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS comments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    parent_id BIGINT DEFAULT NULL,
    content VARCHAR(1000) NOT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_post (post_id),
    KEY idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS post_likes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_post_user (post_id, user_id),
    KEY idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS post_favorites (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_post_user (post_id, user_id),
    KEY idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO posts (id, title, content, images, circle_id, topic_id, author_id, audit_status, like_count, favorite_count, comment_count, view_count) VALUES
(1, '在兔南发现了这家藏在巷子里的面包店', '早起散步时被黄油香气带进了一条小巷。店面不大，玻璃柜里摆着刚出炉的可颂和盐面包，老板娘会记得每位熟客的口味。\n\n推荐：海盐可颂、桂花乳酪贝果。下午两点后通常会售罄，想去的朋友记得早点。', '["/images/jiangnan/zhouzhuang-lanterns.jpg"]', 1, 2, 2, 1, 36, 18, 3, 428),
(2, '滨河公园的落日，值得专门跑一趟', '傍晚六点半从东门进去，沿着河走十五分钟就能看到最好的角度。今天云层很薄，天空从橘色慢慢变成蓝紫色。\n\n建议带一件薄外套，河边风很大。', '["/images/jiangnan/xitang-dusk-1.jpg"]', 2, 1, 3, 1, 52, 24, 2, 691),
(3, '周三夜跑 5 公里，有没有一起的？', '路线：体育中心北门集合，沿滨河步道到白桥折返，配速 6分30左右。\n\n安全第一，不竞速，结束后可以一起吃碗热汤面。', '["/images/jiangnan/wuzhen-river.jpg"]', 2, 3, 4, 1, 19, 6, 2, 203),
(4, '我家猫主子的新纸箱城堡', '快递箱不要扔，剪几个洞就是猫猫快乐屋。它已经在里面住了一下午，连饭都不肯出来吃。', '["/images/jiangnan/zhouzhuang-canal.jpg"]', 3, 1, 4, 1, 28, 11, 1, 335),
(5, '旧相机交换：一台九成新的胶片机', '想换一台轻便的数码相机，也可以直接出。相机功能正常，送两卷胶卷。仅限同城面交。', '["/images/jiangnan/suzhou-canal.jpg"]', 4, 4, 2, 0, 2, 1, 0, 17)
ON DUPLICATE KEY UPDATE title = VALUES(title);

INSERT INTO comments (id, post_id, user_id, parent_id, content, status) VALUES
(1, 1, 3, NULL, '这家我也去过！桂花贝果真的香。', 1),
(2, 1, 4, NULL, '求一个具体地址，周末想去打卡。', 1),
(3, 1, 2, 2, '老槐树巷往里走 50 米，门口有块绿色小招牌。', 1),
(4, 2, 2, NULL, '这个配色也太美了，明天就去。', 1),
(5, 2, 4, NULL, '请问骑车能进去吗？', 1),
(6, 3, 2, NULL, '想参加，不过我只能跑 7 分配速可以吗？', 1),
(7, 3, 4, 6, '当然可以，我们按最舒服的节奏跑。', 1),
(8, 4, 3, NULL, '猫猫：这个家终于是我的了。', 1)
ON DUPLICATE KEY UPDATE content = VALUES(content);

INSERT IGNORE INTO post_likes (post_id, user_id) VALUES (1, 3), (1, 4), (2, 2), (2, 4), (3, 2), (4, 2), (4, 3);
INSERT IGNORE INTO post_favorites (post_id, user_id) VALUES (1, 3), (2, 2), (2, 4), (4, 2);
