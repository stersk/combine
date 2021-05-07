GO
USE [KombineProxyServer]
ALTER TABLE [dbo].[queries] ADD [message_type] [VARCHAR](15) NOT NULL DEFAULT '',
                                [message_token] [VARCHAR](20) NOT NULL DEFAULT ''
GO