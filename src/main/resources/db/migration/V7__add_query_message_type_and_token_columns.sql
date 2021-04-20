GO
USE [KombineProxyServer]
ALTER TABLE [dbo].[queries] ADD [message_type] [VARCHAR] NOT NULL DEFAULT "",
                                [message_token] [VARCHAR] NOT NULL DEFAULT ""
GO