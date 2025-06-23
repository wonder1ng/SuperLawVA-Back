-- users 테이블에 deleted_at 컬럼 추가
ALTER TABLE users ADD COLUMN deleted_at DATETIME NULL;

-- 기존 데이터의 deleted_at을 NULL로 설정
UPDATE users SET deleted_at = NULL WHERE deleted_at IS NULL;

-- 인덱스 추가 (선택사항, 소프트 삭제 쿼리 성능 향상)
-- CREATE INDEX idx_users_deleted_at ON users(deleted_at); 