GO
USE [KombineProxyServer]
ALTER TABLE [dbo].[queries] ADD [processing_error] BIT NOT NULL DEFAULT 0
GO