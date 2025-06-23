-- users 테이블에 deleted_at 컬럼 추가
-- 이 컬럼은 soft delete 기능을 위한 것입니다.

ALTER TABLE users 
ADD COLUMN deleted_at TIMESTAMP NULL DEFAULT NULL 
COMMENT 'Soft delete를 위한 삭제 시각';

-- 인덱스 추가 (성능 향상을 위해)
CREATE INDEX idx_users_deleted_at ON users(deleted_at);

-- 기존 데이터는 모두 deleted_at이 NULL (삭제되지 않음)이므로 별도 업데이트 불필요

SELECT 'deleted_at 컬럼이 성공적으로 추가되었습니다.' as message; 