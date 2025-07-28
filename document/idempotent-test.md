# 데이터 적재 시 멱등성 보장 전략별 성능 및 효율성 비교 분석

## 1. 실험 목표
단일 식별자가 없는 데이터 모델에서, 복합 유니크 키 전략과 해시 컬럼 전략이 멱등성을 보장할 때의 성능과 자원 효율성을 정량적으로 비교 분석한다.
측정된 데이터를 근거로, 복잡한 비즈니스 키를 가진 시스템에 가장 적합한 데이터 적재 아키텍처를 선택하기 위한 기술적 근거를 확보한다.

## 2. 실험 환경 및 설계
데이터 스키마 정의
- 테이블: bank_transaction
- 컬럼:
  - id (PK, BIGINT)
  - transaction_time (TIMESTAMP(6)) - 거래 시간 (분까지)
  - account_number (VARCHAR(30)) - 계좌번호
  - transaction_type (VARCHAR(10)) - 거래 유형 ('DEPOSIT', 'WITHDRAWAL')
  - amount (DECIMAL(18,2)) - 거래 금액
  - balance (DECIMAL(18,2)) - 거래 후 잔액
  - counterparty_name (VARCHAR(100)) - 거래 상대방
  - memo (VARCHAR(255)) - 메모
  - hash_value (VARCHAR(64)) - 시나리오 3 전용, SHA-256 해시 저장
- 유니크 제약조건: 이 시스템에서는 transaction_time, account_number, transaction_type, amount 네 가지 컬럼의 조합이 하나의 거래를 식별하는 고유한 값으로 간주한다.

### 데이터셋 준비
데이터는 ./script/idempotent에 준비합니다.
- A (초기 데이터): 위 스키마에 맞는 10,000 건의 데이터 (CSV 파일)
- B (추가 데이터): A와 9,000 건이 중복되고, 1,000 건이 새로운 데이터 (CSV 파일). 중복 데이터와 새로운 데이터의 순서는 랜덤이어야 합니다.

### 환경 사양 명시

서버: Apple OS, M2 Pro, 16GB Memory
S/W 버전: Spring Boot, PostgreSQL, JPA, Querydsl

## 3. 테스트 시나리오
공통 전제: 데이터셋 B(10,000 건)를 DB에 적재하는 과정을 측정한다. DB에는 사전에 데이터셋 A(10,000 건)가 저장되어 있다.
배치 크기: 1,000건씩 10회를 기준으로 합니다.
인덱스: 인덱스가 걸려 있는 테스트와 걸려있지 않은 테스트를 비교합니다. 인덱스 유무에 따른 성능 차이도 로깅합니다.

### 시나리오 1-1: Application-level SELECT 후 INSERT (비교군)
- 로직: 애플리케이션이 각 데이터 건마다 SELECT로 존재 여부를 확인 후 INSERT를 실행한다. 복합 키 때문에 IN (...) 절을 사용하기 어려우므로, 건별 처리가 불가피하며 이는 성능 비교를 위한 베이스라인(최악의 경우) 역할을 한다.
- 실행:
  1. 데이터셋 B의 각 행(row)에 대해 SELECT 1 FROM bank_transaction WHERE transaction_time = ? AND account_number = ? AND transaction_type = ? AND amount = ? 쿼리를 실행한다. 
  2. 조회 결과가 없을 경우에만 해당 행을 INSERT 한다.

#### 시나리오 1-2: INDEX 존재
```sql
CREATE INDEX idx_bank_transaction_lookup 
ON bank_transaction (transaction_time, account_number, transaction_type, amount);
```

### 시나리오 2-1: ON CONFLICT DO NOTHING 활용 (복합 유니크 키, 인덱스 적용)
- 로직: 데이터베이스의 복합 유니크 키 제약조건과 ON CONFLICT 절을 직접 활용한다.
- 실행:
  1. bank_transaction 테이블에 UNIQUE (transaction_time, account_number, transaction_type, amount) 제약조건을 설정한다. 
  2. 애플리케이션은 데이터셋 B 전체에 대해 INSERT ... ON CONFLICT (transaction_time, account_number, transaction_type, amount) DO NOTHING 구문을 포함한 **Batch INSERT**를 한 번에 실행한다.

### 시나리오 2-2: 추가 인덱스 적용
```sql
-- UNIQUE 제약조건 (자동 인덱스)
ALTER TABLE bank_transaction 
ADD CONSTRAINT uk_transaction_composite 
UNIQUE (transaction_time, account_number, transaction_type, amount);

-- 추가 조회용 인덱스 (컬럼 순서 최적화)
CREATE INDEX idx_transaction_optimized
ON bank_transaction (account_number, transaction_time, transaction_type, amount);
```

### 시나리오 3-1: ON CONFLICT DO NOTHING 활용 (Hashed Unique Index)
- 로직: 복잡한 복합 키를 단일 해시 값으로 변환하여 인덱싱 및 충돌 검사 효율을 높인다.
- 실행:
  1. bank_transaction 테이블의 hash_value 컬럼에 UNIQUE 인덱스를 생성한다. 
  2. 애플리케이션은 데이터셋 B의 각 행에 대해, 복합 키를 구성하는 4개 컬럼 (transaction_time 등)의 값을 조합하여 SHA-256 해시 값을 계산하고 hash_value에 채워 넣는다. 
  3. 데이터셋 B 전체에 대해 INSERT ... ON CONFLICT (hash_value) DO NOTHING 구문을 포함한 **Batch INSERT**를 한 번에 실행한다.

### 시나리오 3-2: 추가 인덱스 적용
```sql
-- 해시 UNIQUE 인덱스
CREATE UNIQUE INDEX uk_transaction_hash 
ON bank_transaction (hash_value);

-- 원본 복합 키 인덱스 (조회 최적화용)
CREATE INDEX idx_transaction_original
ON bank_transaction (transaction_time, account_number, transaction_type, amount);
```

### 시나리오 4-1: Staging Table을 이용한 MERGE (또는 UPSERT)
- 실행:
  1. Load: 새로 들어온 데이터(데이터셋 B)를 실제 bank_transaction 테이블이 아닌, 구조가 동일한 임시 테이블(bank_transaction_staging)에 일단 모두 BATCH_INSERT 합니다. 이 과정은 아무런 제약 조건 없이 빠르게 수행됩니다. 
  2. Merge: 임시 테이블의 데이터를 실제 테이블로 합칩니다. PostgreSQL의 INSERT ... ON CONFLICT DO UPDATE 구문이나 다른 DB의 MERGE 구문을 사용합니다. 
     - 신규 데이터: bank_transaction에 없는 데이터는 INSERT 합니다. 
     - 중복 데이터: bank_transaction에 이미 있는 데이터는 UPDATE 하거나(DO UPDATE SET ...), 그냥 무시합니다(DO NOTHING). 
  3. Truncate: 작업이 끝나면 임시 테이블(bank_transaction_staging)을 비웁니다.

### 시나리오 4-2: INDEX 존재
```sql
CREATE TABLE bank_transaction_staging (LIKE bank_transaction);
CREATE INDEX idx_staging_merge
ON bank_transaction_staging (transaction_time, account_number, transaction_type, amount);
```

## 4. 측정 지표 및 검증 방법
- 데이터베이스 버퍼 캐시 상태는 테스트마다 동일해야합니다.
  - 각 테스트 전 데이터베이스를 재시작합니다.
  - 시스템 캐시를 클리어 합니다.
- 시나리오 3를 진행할 경우 해시 충돌 검증이 필요합니다.
- 각 시나리오별 3-5회 반복 실행하여, 실행 결과를 로깅합니다.
  - 최종적으로 평균값도 로깅합니다.
- 각 시나리오별 pg_stat_statements를 활용한 쿼리 분석과 실행 계획에 대한 기록을 로깅합니다.

### 성능 지표 (Quantitative Metrics)
- 총 처리 시간 (초): B 데이터셋 전체를 처리하는 데 걸리는 시간
- 처리량 (RPS): 초당 처리한 레코드 수 (10,000건 / 총 처리 시간)
- App 리소스: CPU 사용률(%), JVM Heap Memory 사용량(MB), GC 통계
- DB 리소스: CPU 사용률(%), I/O 사용량(IOPS)

### 정확성 검증 (Correctness Verification)
- 최종 SELECT COUNT(*) FROM bank_transaction; 결과가 1.1만 건인지 확인한다.
- 데이터셋 B에만 존재하던 신규 데이터 중 일부가 정상적으로 INSERT 되었는지 샘플링하여 확인한다.

