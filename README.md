# training2018-final-project

*プロジェクト作成の流れ*
1. 計画 (0.5日)
2. 環境構築 (0.5日)
⋅⋅* Docker image 作成
-sbt project (with Play & Slick)
-Angular project
⋅⋅* AWSでの環境構築
-IAM, VPC
-Docker image をECS にpush
-RDSでDatabaseの初期化
-S3でFile systemの初期化
3. サービスの構成計画 (1日)
⋅⋅* サービスロジックを計画
⋅⋅* クラス構成を計画
⋅⋅* フロントのストーリーボードを設計
4. DB設計 (1日)
⋅⋅* ERFluteを運用してSchemaを設計する
⋅⋅* SQLの実装
5. Phase 1 の実装: UserのCRUD (2日)
6. Phase 2 の実装: RestaurantのCRUD (2日)
7. Phase 3 の実装: Service logic (4日)
8. Phase 4 の実装: UX UI Design (2日)
9. セキュリティー(1日)
