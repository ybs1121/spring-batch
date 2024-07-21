## **1.SpringBatch 기본 이해**

- **SpringBatch의 개념, 구조, 구성요소 학습**
- **Job, Step, Tasklet, Chunk 등의 개념 이해**

1. **SpringBatch 개념**
    - SpringBatch는 대량의 데이터 처리를 위한 프레임 워크이다.
    - 배치 작업의 실행, 모니터링, 오류 처리 등의 기능을 제공합니다.
    - 대용량 데이터 처리 , ETL**(Extract-Transform-Load)**  작업, 정기 백업 등에 주로 사용됩니다.
2. **SpringBatch 구조**
    - **Job** : 전체 배치 작업의 단위이다. 하나 이상의 Step으로 구성됩니다.
    - **Step** : 배치 작업의 개별 단계입니다. Reader, Processor, Writer로 구성됩니다.
    - **Reader** : 데이터 입력 소스 (파일, DB 등)로부터 데이터를 읽어옵니다.
    - **Processor** : 읽어온 데이터를 가공/변환 합니다.
    - **Writer** : 가공된 데이터를 출력 대상(파일, DB 등)에 저장합니다.
3. **SpringBatch의 구성요소**
    - **Tasklet** : Step 내에 단일 태스크를 수행하는 컴포넌트입니다.
    - **Chuck** : Reader에서 읽어온 데이터를 한 번에 처리하는 단위입니다.
    - **JobRepository** : 배치 작업의 실행 정보와 상태를 저장하고 관리합니다.
    - **JobLauncher** : 배치 작업을 실행하는 컴포넌트 입니다.
    - **JobExecutionListener** : 배치 작업 실행 전/후 커스텀 로직을 수행할 수 있습니다.
4. **주요 개념의 이해**
    - **Job** : 배치 작업의 최상단 단위로, 하나 이상의 Step으로 구성됩니다.
    - **Step** : 배치 작업의 개별 단계로, Reader - Processor - Writer 로 구성됩니다.
        - Step은 배치 작업의 개별 단계를 나타내는 개념이며, 이 단계에는 Reader, Processor, Writer 중 필요한 컴포넌트만 포함될 수 있습니다.
    - **Tasklet** : Step 내에서 단일 태스크를 수행하는 컴포넌트 입니다.
    - **Chunk** : Reader에서 읽어온 데이터를 한 번에 처리하는 단위입니다.
- 참고 내용
````
1. ETL(Extract-Transform-Load)은 데이터 통합 프로세스의 3가지 주요 단계를 나타내는 용어

- Extract (추출)
  데이터 소스로부터 필요한 데이터를 추출하는 단계입니다.
  다양한 데이터 소스(데이터베이스, 파일, API 등)로부터 데이터를 가져옵니다.
  추출된 데이터는 중간 저장소나 메모리에 임시 저장됩니다.

- Transform (변환)
  추출된 데이터를 분석하고 정제하는 단계입니다.
  데이터 형식 변환, 데이터 정제, 데이터 통합 등의 작업이 이루어집니다.
  변환 규칙에 따라 데이터를 가공하여 최종 데이터 형태로 만듭니다.

- Load (적재)
  변환된 데이터를 최종 데이터 저장소(데이터베이스, 데이터 웨어하우스 등)에 저장하는 단계입니다.
  데이터를 적재하는 과정에서 데이터 무결성 검사, 데이터 중복 제거 등의 작업이 수행됩니다.
  최종적으로 데이터가 저장되어 분석 및 활용이 가능해집니다.
  ETL 프로세스는 다양한 데이터 소스로부터 데이터를 통합하고 정제하여 분석 가능한 형태로 만드는 것이
  주된 목적입니다.  이를 통해 데이터 기반 의사결정을 지원할 수 있습니다

Tasklet

Taskletdptj "단일 테스크"라는 것은 하나의 논리적인 작업 단위를 말합니다. 예를 들면:

DB에서 데이터 가져오기: Reader가 데이터베이스에서 데이터를 Select하여 가져오는 것은 하나의 단일 테스크입니다.

데이터 변환하기: Processor가 가져온 데이터를 가공하고 변환하는 것도 하나의 단일 테스크입니다.

데이터 저장하기: Writer가 변환된 데이터를 데이터베이스나 파일에 저장하는 것도 단일 테스크에 해당합니다.

이처럼 ETL 프로세스의 각 단계(Extract, Transform, Load)에서 수행되는 개별적인 작업들이 단일 테스크에 해당합니다.
Tasklet은 이러한 단일 테스크를 캡슐화하여 Step 내에서 실행할 수 있게 해줍니다.

단일 테스크는 독립적으로 실행될 수 있고, 실패 시 재시도할 수 있는 단위 작업을 의미합니다.
이를 통해 ETL 프로세스의 안정성과 확장성을 높일 수 있습니다.

좀 더 구체적으로

Reader Tasklet

Reader가 데이터를 읽어오는 단일 작업을 나타냅니다.
예를 들어 DB에서 데이터를 Select하는 작업이 Reader Tasklet에 해당합니다.
Processor Tasklet

Processor가 데이터를 변환/가공하는 단일 작업을 나타냅니다.
예를 들어 데이터 형식 변환, 데이터 정제 등의 작업이 Processor Tasklet에 해당합니다.
Writer Tasklet

Writer가 데이터를 저장하는 단일 작업을 나타냅니다.
예를 들어 변환된 데이터를 DB에 Insert하는 작업이 Writer Tasklet에 해당합니다.
````

포멧이 동일할 때 
```` java
@Bean
@Qualifier("uppercaseProcessor")
public ItemProcessor<Person, Person> uppercaseProcessor() {
    return person -> {
        person.setName(person.getName().toUpperCase());
        return person;
    };
}

@Bean
@Qualifier("lowercaseProcessor")
public ItemProcessor<Person, Person> lowercaseProcessor() {
    return person -> {
        person.setName(person.getName().toLowerCase());
        return person;
    };
}


@Bean
public Job job(JobBuilderFactory jobBuilderFactory,
               StepBuilderFactory stepBuilderFactory,
               @Qualifier("uppercaseProcessor") ItemProcessor<Person, Person> itemProcessor,
               ItemWriter<Person> itemWriter) {
    return jobBuilderFactory.get("personJob")
            .start(step(stepBuilderFactory, itemProcessor, itemWriter))
            .build();
}


````

