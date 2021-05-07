GO
USE [KombineProxyServer]
ALTER TABLE [dbo].[queries] ADD [message_user_id] [VARCHAR](36) NOT NULL DEFAULT ''
GO