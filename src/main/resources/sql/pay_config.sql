/*
 Navicat Premium Data Transfer

 Source Server         : os1
 Source Server Type    : MySQL
 Source Server Version : 80015
 Source Host           : 192.168.1.77:3307
 Source Schema         : pay

 Target Server Type    : MySQL
 Target Server Version : 80015
 File Encoding         : 65001

 Date: 12/07/2022 14:28:24
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for pay_config
-- ----------------------------
DROP TABLE IF EXISTS `pay_config`;
CREATE TABLE `pay_config`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `config_type` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '支付配置类型',
  `app_id` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '微信服务号id/小程序id//支付宝应用号',
  `notify_url` json NULL COMMENT '异步通知地址',
  `app_secret` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '微信-小程序secret',
  `mch_id` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '微信支付-商户号',
  `api_key` varchar(600) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '微信支付-秘钥',
  `cert_store_path` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '微信支付证书地址',
  `rsa2_private_key` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '支付宝支付-RSA2私钥',
  `ali_pay_public_key` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '支付宝支付-公钥',
  `merchant_id` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '建行商户平台商户代码',
  `pos_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '建行商户平台商户柜台代码',
  `branch_id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '建行（省）分行代码',
  `pub_key` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '建行商户平台商户公钥',
  `operator` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '建行商户平台商户操作员编号',
  `operator_password` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '建行商户平台商户操作员密码',
  `ebs_host` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '外联平台地址',
  `cert_file_path` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '建行商户操作员证书存放的绝对路径',
  `cert_file_password` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '建行商户操作员证书密码',
  `config_file_path` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '建行商户xml配置文件存放的绝对路径',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of pay_config
-- ----------------------------
INSERT INTO `pay_config` VALUES (1, 'WXPayConfig', 'wx18aac544dfe6d8b3', '{\"GOODS\": \"http://t6ujm6.natappfree.cc/campus_management/wxpay/notify_meetingOrder\"}', 'f54f82f5d384302edb9288e1ec1bf8c8', '1620609919', 'P3jHHoG0ZfBKW4Xb7MQJcNPviFQcc90u', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `pay_config` VALUES (2, 'AliPayConfig', '2021003121625110', '{\"MEETINGORDER\": \"http://85rzbxr.natappfree.cc/campus_management/aliPay/notify_meetingOrder\"}', NULL, NULL, NULL, NULL, 'MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCHpuZFBIdPF3vx57IQ8duZ4rQTeIA4MEslwVdcGWWtjZYz1zfqb3DdEAUoPOYqOYecvLAZFBMRlxGHCe+7dtxzyYXGjFMmKHqe5EdcTBTEwFHzQUPol9/nXXgA/7BBLGPRrP/Y2jPjatJXTFkvAXR1ky3W0K5kDFIIk0k/cMWwKtT7ezNQHqknTBF7pIP93nWzICAJadKRRzHNOYBbJyO4qJyv/yT/LrL92QYlIHm+eMY7SaEi0rVewF77UX573oAOk89T+7wl42RqQP9bZfUdnUzzwdFdajqkd706Hk9Eer/XOU5t+OWbA6wu7vekM/O6OJkKui+0KtZ+lLZdi5SJAgMBAAECggEBAIZAsvMpmTS4vE1cjsngZAN379q5TsTNXwI9yFT5Ob07PKWP4eIeaugOO9xn8S9nAoUn5WzchkbJaWppdh7lcycRElm/dWd1dm4c5Vc+YOucnz6NoETpUoqr4eGzLGBEK4JPomKfCe2QQh5uDHHOEdFvWVWZ8I5J90JPapXj6XMAYhs3Qn5r45ZMp6py4OUoy0rhdrCsAJ0BSGhwmjOa0iFuwX8rhOV4xFam1LbLES/gKYXcwXfRpkU8OH9PPAsGXomF+6J7r4dXoXbdflGBvHiWDL6smSuB1OS+EIweHNGadT5pNSmuuAByVmw8ivxcBDAffd7yHgHBqu0QqIcVj9ECgYEA9W3zJpWGW7gyZ6tOPmZWSASztlnCaSFfB0bLxewOc58nH2PqsixGgagcXyNYvS3Lpnm28mmWiG1W68FBD08Ed6LQAZvHjePF8WOnnsfjqFD1tjmkQoyeWBUWYIR2BjCpbaJJq3FzPN35i1rF7y5Z8rtguLcAbC9P2xat7MinYB0CgYEAjX6ROjfbMs+3KyU+mthIN5TaO6427I2dmoL7ofRYAE26TCeUMJM8pjZ+GbWYEulnumE/a9taA4dUP5diw+gBFtbjvcWwatLHuzRrO7lW/qM936eunG+anT8LlT6KjLx/z3BEoIIY+n5wu39nly9F5/SuZvpTDL0Kc+PaBlZYMl0CgYEAyZlkNphyQhr+xGtRnS4gx42dNTdr20F/NpHvxodNo6ni0AUZ/vshe1Vk4L0/Ij5g30FxhpQ3A5+U25jl4TZN/Buv8vJMLAAiU1NBl8qn9VVzUBjvFX8+finsNKH59MG6GoVJMFvDhgbr+Y05s2uGlT1XiMNBxJvkY5mwiiSUX/0CgYBXk1Xp1baLHXs+bh/wkRspo6zrMWn3QbOhdZ6vX9+z2GgNSJX09rycF7A202mbkvFLr3Mkm7B77J/deCJ+JLO/4iM1Sw1lIK7UlJ4mahGJK5pZOpzdYEovN11A82OntefZiaBARCWXrPizZCjGr1CyP4ROYnNSuBKnJgjE0V5nVQKBgEW6z4oWgWQsJC81e7S6UaMY9OWEic0TcCuF6FK07g+7UwRSDSSP0MmTNH1Yk8Qx9iut7OFUA6gFXS7nBlDXcczD6JL/sVNgWDBCdViE2IzOTXhImgaEsx0O+LA0qEWMV8Ms0dD/vnZRwcQJyVnO3WXPdN9NXHdJNZWajT+AzYCt', 'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAk1owYh2VrD41BVaektkczwCeMJfIzB5tpRSb+QpexwaMqnN82ljLJHNbZ98JBU1vNdHQ27czvsCZoQyLDm1ykTCI8FB5cGejbvmANrHWDdM6sRMyr0cDUgtyhtrkQuqIh15a9L7BmBaD9CUC6bLMm28mSESW+l01bq9UZ47PKQfmNH8LEC6+jg8d3xczjIG/WTTkyTf+E1wGeEd0QNY+lJmKmvPpT2T8oh26MswT1UZ7m7tF7rWYgqYnBqHzMp94wwLORp567c1wpy4iwXhSmSJB+GXsnjcXkyKw6u2s/57tiVFEEijf1rueQniY8yEls9+1T3nz0X7YY/WnDuWUfQIDAQAB', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `pay_config` VALUES (3, 'CCBPayConfig', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '105000173726213', '066367494', '320000000', '30819c300d06092a864886f70d010101050003818a00308186028180562c014bfec4db86bfb235a0f3d9dd026a45afe599aba3bbbe92bec60bc9d92ca2bb66aaf6892cace43d4238a6ece2a11c5fef16e56f48e31fff56ae451e3b6d1a43d3744ab0006e8d76ed2869b1a1f30e77c32e195697ca0310adcde1450fa123a7d8f5528dacce53871704b45a95b64f0f0ee4e2d8d8cf1366ab3dae050329020113', '105000173726213-L41', 'i9twJTMFoaUwgvVwaxxe6g==', '192.168.1.34:8888', 'xxxxx', '4ohqrNS0+VVII/f1SxVRHA==', 'yyyyy');

SET FOREIGN_KEY_CHECKS = 1;
