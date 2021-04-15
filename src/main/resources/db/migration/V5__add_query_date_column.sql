GO
USE [KombineProxyServer]
ALTER TABLE [dbo].[queries] ADD [request_date] DATETIME2 NOT NULL DEFAULT GETDATE()
GO