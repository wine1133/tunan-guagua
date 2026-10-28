USE guagua_content;
UPDATE circles SET cover = CASE id
  WHEN 1 THEN '/images/jiangnan/zhouzhuang-boat.jpg'
  WHEN 2 THEN '/images/jiangnan/wuzhen-river.jpg'
  WHEN 3 THEN '/images/jiangnan/zhouzhuang-canal.jpg'
  WHEN 4 THEN '/images/jiangnan/jiangnan-garden.jpg'
  ELSE cover END;
UPDATE topics SET cover = CASE id
  WHEN 1 THEN '/images/jiangnan/xitang-dusk-1.jpg'
  WHEN 2 THEN '/images/jiangnan/tuisi-garden.jpg'
  WHEN 3 THEN '/images/jiangnan/suzhou-canal.jpg'
  WHEN 4 THEN '/images/jiangnan/qibao-canal.jpg'
  ELSE cover END;
UPDATE banners SET image_url = CASE id
  WHEN 1 THEN '/images/jiangnan/xitang-dusk-2.jpg'
  WHEN 2 THEN '/images/jiangnan/wuzhen-aerial.jpg'
  WHEN 3 THEN '/images/jiangnan/zhouzhuang-lanterns.jpg'
  ELSE image_url END;
UPDATE news SET cover = CASE id
  WHEN 1 THEN '/images/jiangnan/xitang-dusk-2.jpg'
  WHEN 2 THEN '/images/jiangnan/wuzhen-river.jpg'
  WHEN 3 THEN '/images/jiangnan/suzhou-canal.jpg'
  ELSE cover END;

USE guagua_post;
UPDATE posts SET images = CASE id
  WHEN 1 THEN '["/images/jiangnan/zhouzhuang-lanterns.jpg"]'
  WHEN 2 THEN '["/images/jiangnan/xitang-dusk-1.jpg"]'
  WHEN 3 THEN '["/images/jiangnan/wuzhen-river.jpg"]'
  WHEN 4 THEN '["/images/jiangnan/zhouzhuang-canal.jpg"]'
  WHEN 5 THEN '["/images/jiangnan/suzhou-canal.jpg"]'
  ELSE images END;
