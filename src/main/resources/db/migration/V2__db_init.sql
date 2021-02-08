USE [master]
GO
/****** Object:  Database [combineProxyServer]    Script Date: 08.02.2021 18:50:30 ******/
EXEC sys.sp_db_vardecimal_storage_format N'combineProxyServer', N'ON'
GO
USE [combineProxyServer]
GO
/****** Object:  Sequence [dbo].[query_sequence]    Script Date: 08.02.2021 18:50:30 ******/
CREATE SEQUENCE [dbo].[query_sequence]
 AS [bigint]
 START WITH 1
 INCREMENT BY 1
 MINVALUE -9223372036854775808
 MAXVALUE 9223372036854775807
 CACHE
GO
/****** Object:  Table [dbo].[queries]    Script Date: 08.02.2021 18:50:30 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[queries](
	[id] [bigint] NOT NULL,
	[account] [varchar](255) NULL,
	[body] [varchar](max) NULL,
	[signature] [varchar](255) NULL,
PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Index [flyway_schema_history_s_idx]    Script Date: 08.02.2021 18:50:30 ******/
USE [master]
GO
ALTER DATABASE [combineProxyServer] SET  READ_WRITE
GO