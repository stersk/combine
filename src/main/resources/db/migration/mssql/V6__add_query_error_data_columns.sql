GO
USE [KombineProxyServer]
ALTER TABLE [dbo].[queries] ADD [processing_result_code] INT DEFAULT 0,
                                [processing_date] DATETIME2,
                                [processing_error_message] [VARCHAR](max),
                                [retry] BIT
GO
UPDATE [dbo].[queries]
SET processing_date = GETDATE(),
    retry = 1,
    processing_result_code = 501
WHERE processing_error = 1
GO