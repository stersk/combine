USE [master]
GO
USE [KombineProxyServer]
GO
/****** Object:  Table [dbo].[properties] ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[properties](
	[name] [varchar](255) NOT NULL,
	[value] [varchar](max) NOT NULL,
PRIMARY KEY CLUSTERED
(
	[name] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Index [flyway_schema_history_s_idx]    Script Date: 08.02.2021 18:50:30 ******/
USE [master]
GO
ALTER DATABASE [KombineProxyServer] SET  READ_WRITE
GO