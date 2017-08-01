#if !defined(BR_ANYCHAT_SERVER_SDK_H__INCLUDED_)
#define BR_ANYCHAT_SERVER_SDK_H__INCLUDED_

#include "typedef.h"

/**
 *	AnyChat Server SDK Include File
 */


#pragma once


#define BRAS_API extern "C"


#define BRAS_SERVERAPPMSG_CONNECTED			1	///< ����AnyChat�������ɹ�
#define BRAS_SERVERAPPMSG_DISCONNECT		2	///< ��AnyChat�������Ͽ�����

// ��������Ϣ���Ͷ��壨�ص��¼���BRAS_OnServerAppMessageEx_CallBack ������
#define BRAS_MESSAGE_CORESERVERCONN			10	///< ����ķ�������������Ϣ��wParamΪerrorcode
#define BRAS_MESSAGE_RECORDSERVERCONN		11	///< ��¼���������������Ϣ��wParamΪerrorcode��lParamΪrecordserverid
#define BRAS_MESSAGE_LOGINSERVERCONN		12	///< ���¼��������������Ϣ��wParamΪerrorcode��lParamΪloginserverid
#define BRAS_MESSAGE_ROOMSERVERCONN			13	///< �뷿���������������Ϣ��wParamΪerrorcode��lParamΪroomserverid
#define BRAS_MESSAGE_MEDIASERVERCONN		14	///< ����ý���������������Ϣ��wParamΪerrorcode��lParamΪmediaserverid

// ��Ƶ�����¼����Ͷ��壨API��BRAS_VideoCallControl ���������OnVideoCallEvent�ص�������
#define BRAS_VIDEOCALL_EVENT_REQUEST		1	///< ��������
#define BRAS_VIDEOCALL_EVENT_REPLY			2	///< ��������ظ�
#define BRAS_VIDEOCALL_EVENT_START			3	///< ��Ƶ���лỰ��ʼ�¼�
#define BRAS_VIDEOCALL_EVENT_FINISH			4	///< �Ҷϣ����������лỰ

// �û���Ϣ�������Ͷ��壨API��BRAS_UserInfoControl ���������OnUserInfoControl�ص�������
#define BRAS_USERINFO_CTRLCODE_KICKOUT		1	///< ��ָ���û���ϵͳ���ߵ�
#define BRAS_USERINFO_CTRLCODE_SYNCDATA		2	///< ��ָ���û�������ͬ�����ͻ���
#define BRAS_USERINFO_CTRLCODE_FUNCCTRL		3	///< �ͻ��˹��ܿ��ƣ�wParamΪ���ܲ������
#define BRAS_USERINFO_CTRLCODE_BLOCKIP		4	///< ��ֹIP��ַ���ӷ�������lpStrValueΪIP��ַ�ַ�����֧��ͨ�����*��
#define BRAS_USERINFO_CTRLCODE_UNBLOCKIP	5	///< ����IP��ַ���ӷ�������lpStrValueΪIP��ַ�ַ�����֧��ͨ�����*��

#define BRAS_USERINFO_CTRLCODE_ADDGROUP		20	///< ����û����飬wParamΪ����Id��lpStrValueΪ��������
#define BRAS_USERINFO_CTRLCODE_DELGROUP		21	///< ɾ���û����飬wParamΪ����Id
#define BRAS_USERINFO_CTRLCODE_ADDFRIEND	22	///< ����û����ѣ�wParamΪ����Id
#define BRAS_USERINFO_CTRLCODE_DELFRIEND	23	///< ɾ���û����ѣ�wParamΪ����Id
#define BRAS_USERINFO_CTRLCODE_SETGROUPRELATION	24	///< ���ú��������Ĺ�����ϵ��wParamΪ����Id��lParamΪ����Id����ʾ��������ĳ������

#define BRAS_USERINFO_CTRLCODE_APPDEFINE	100	///< Ӧ�ò��Զ�����ʼָ��

// �ں˲������ó������壨API��BRAS_SetSDKOption ���������
#define BRAS_SO_GETTRANSBUFTIMESTAMP		1	///< ��ȡ͸��ͨ��ʱ���
#define BRAS_SO_RECORD_VIDEOBR				2	///< ¼����Ƶ�������ã�����Ϊ��int�ͣ���λ��bps��
#define BRAS_SO_RECORD_AUDIOBR				3	///< ¼����Ƶ�������ã�����Ϊ��int�ͣ���λ��bps��
#define BRAS_SO_RECORD_FILETYPE				4	///< ¼���ļ��������ã�����Ϊ��int�ͣ� 0 MP4[Ĭ��], 1 WMV, 2 FLV, 3 MP3��
#define BRAS_SO_RECORD_WIDTH				5	///< ¼����Ƶ������ã�����Ϊ��int�ͣ��磺320��
#define BRAS_SO_RECORD_HEIGHT				6	///< ¼���ļ��߶����ã�����Ϊ��int�ͣ��磺240��
#define BRAS_SO_RECORD_FILENAMERULE			7	///< ¼���ļ����������򣨲���Ϊ��int�ͣ�

// ��ʼ����־��API��BRAS_InitSDK ���������
#define BRAS_INITFLAGS_MULTITHREADS		0x01	///< ���߳�ģʽ

// �ص��������Ͷ��壨API��BRAS_SetCallBack ���������
#define BRAS_CBTYPE_APPMESSAGE				1	///< ������Ӧ�ó�����Ϣ�ص�
#define BRAS_CBTYPE_APPMESSAGEEX			2	///< ������Ӧ�ó�����Ϣ����չ���ص�
#define BRAS_CBTYPE_ONTIMER					3	///< SDK��ʱ���ص�
#define BRAS_CBTYPE_VERIFYUSER				4	///< �û������֤�ص�
#define BRAS_CBTYPE_PERPAREENTERROOM		5	///< �û�������뷿��ص�
#define BRAS_CBTYPE_USERLOGIN				6	///< �û���¼�ɹ��ص�
#define BRAS_CBTYPE_USERLOGOUT				7	///< �û�ע���ص�
#define BRAS_CBTYPE_USERLOGOUTEX			8	///< �û�ע������չ���ص�
#define BRAS_CBTYPE_USERENTERROOM			9	///< �û����뷿��ص�
#define BRAS_CBTYPE_USERLEAVEROOM			10	///< �û��뿪����ص�
#define BRAS_CBTYPE_FILTERDATA				11	///< �ϲ�ҵ���Զ������ݻص�
#define BRAS_CBTYPE_TEXTMESSAGE				12	///< ��������ͨ�����ݻص�
#define BRAS_CBTYPE_TRANSBUFFER				13	///< ͸��ͨ�����ݻص�
#define BRAS_CBTYPE_TRANSBUFFEREX			14	///< ͸��ͨ��������չ�ص�
#define BRAS_CBTYPE_TRANSFILE				15	///< �ļ�����ص�����
#define BRAS_CBTYPE_SERVERRECORD			16	///< ������¼��ص�
#define BRAS_CBTYPE_SERVERRECORDEX			17	///< ������¼����չ���ص�
#define BRAS_CBTYPE_VIDEOCALL				18	///< ��Ƶͨ����Ϣ֪ͨ�ص�
#define BRAS_CBTYPE_USERINFOCTRL			19	///< �û���Ϣ���ƻص�


// ¼���ܱ�־���壨API��BRAS_StreamRecordCtrl ���������
#define ANYCHAT_RECORD_FLAGS_VIDEO		0x00000001	///< ¼����Ƶ
#define ANYCHAT_RECORD_FLAGS_AUDIO		0x00000002	///< ¼����Ƶ
#define ANYCHAT_RECORD_FLAGS_SERVER		0x00000004	///< ��������¼��
#define ANYCHAT_RECORD_FLAGS_MIXAUDIO	0x00000010	///< ¼����Ƶʱ���������˵�����������¼��
#define ANYCHAT_RECORD_FLAGS_MIXVIDEO	0x00000020	///< ¼����Ƶʱ���������˵���Ƶ���Ӻ�¼��
#define ANYCHAT_RECORD_FLAGS_ABREAST	0x00000100	///< ¼����Ƶʱ���������˵���Ƶ����¼��
#define ANYCHAT_RECORD_FLAGS_STEREO		0x00000200	///< ¼����Ƶʱ���������˵��������Ϊ��������¼��
#define ANYCHAT_RECORD_FLAGS_SNAPSHOT	0x00000400	///< ����
#define ANYCHAT_RECORD_FLAGS_LOCALCB	0x00000800	///< �������ػص�
#define ANYCHAT_RECORD_FLAGS_STREAM		0x00001000	///< ����Ƶ������¼�ƣ�Ч�ʸߣ������ܴ�����Ƶ������ת�����⣩


// ������Ӧ�ó�����Ϣ�ص���������
typedef void (CALLBACK* BRAS_OnServerAppMessage_CallBack)(DWORD dwMsg, LPVOID lpUserValue);
// ������Ӧ�ó�����Ϣ����չ���ص���������
typedef void (CALLBACK* BRAS_OnServerAppMessageEx_CallBack)(DWORD dwNotifyMessage, DWORD wParam, DWORD lParam, LPVOID lpUserValue);
// SDK��ʱ���ص��������壨�ϲ�Ӧ�ÿ����ڸûص��д���ʱ���񣬶�����Ҫ���⿪���̣߳����Ƕ�ʱ����
typedef void (CALLBACK* BRAS_OnTimerEvent_CallBack)(LPVOID lpUserValue);

// �û������֤�ص���������
typedef DWORD (CALLBACK* BRAS_VerifyUser_CallBack)(LPCTSTR lpUserName,LPCTSTR lpPassword, LPDWORD lpUserID, LPDWORD lpUserLevel, LPTSTR lpNickName,DWORD dwNCLen, LPVOID lpUserValue);
// �û�������뷿��ص���������
typedef DWORD (CALLBACK* BRAS_PrepareEnterRoom_CallBack)(DWORD dwUserId, DWORD dwRoomId, LPCTSTR lpRoomName,LPCTSTR lpPassword, LPVOID lpUserValue);
// �û���¼�ɹ��ص���������
typedef void (CALLBACK* BRAS_OnUserLoginAction_CallBack)(DWORD dwUserId, LPCTSTR szUserName, DWORD dwLevel, LPCTSTR szIpAddr, LPVOID lpUserValue);
// �û�ע���ص���������
typedef void (CALLBACK* BRAS_OnUserLogoutAction_CallBack)(DWORD dwUserId, LPVOID lpUserValue);
// �û�ע����չ�ص��������壨����ע���Ĵ�����룩
typedef void (CALLBACK* BRAS_OnUserLogoutActionEx_CallBack)(DWORD dwUserId, DWORD dwErrorCode, LPVOID lpUserValue);
// �û����뷿��ص���������
typedef void (CALLBACK* BRAS_OnUserEnterRoomAction_CallBack)(DWORD dwUserId, DWORD dwRoomId, LPVOID lpUserValue);
// �û��뿪����ص���������
typedef void (CALLBACK* BRAS_OnUserLeaveRoomAction_CallBack)(DWORD dwUserId, DWORD dwRoomId, LPVOID lpUserValue);
// �ϲ�ҵ���Զ������ݻص���������
typedef void (CALLBACK* BRAS_OnRecvUserFilterData_CallBack)(DWORD dwUserId, BYTE* lpBuf, DWORD dwLen, LPVOID lpUserValue);
// �յ��û���������ͨ�����ݻص���������
typedef void (CALLBACK* BRAS_OnRecvUserTextMsg_CallBack)(DWORD dwRoomId, DWORD dwSrcUserId, DWORD dwTarUserId, BOOL bSecret, LPCTSTR lpTextMessage, DWORD dwLen, LPVOID lpUserValue);
// ͸��ͨ�����ݻص���������
typedef void (CALLBACK * BRAS_OnTransBuffer_CallBack)(DWORD dwUserId, LPBYTE lpBuf, DWORD dwLen, LPVOID lpUserValue);
// ͸��ͨ��������չ�ص���������
typedef void (CALLBACK * BRAS_OnTransBufferEx_CallBack)(DWORD dwUserId, LPBYTE lpBuf, DWORD dwLen, DWORD wParam, DWORD lParam, DWORD dwTaskId, LPVOID lpUserValue);
// �ļ�����ص���������
typedef void (CALLBACK * BRAS_OnTransFile_CallBack)(DWORD dwUserId, LPCTSTR lpFileName, LPCTSTR lpTempFilePath, DWORD dwFileLength, DWORD wParam, DWORD lParam, DWORD dwTaskId, LPVOID lpUserValue);
// ������¼��ص���������
typedef void (CALLBACK * BRAS_OnServerRecord_CallBack)(DWORD dwUserId, DWORD dwParam, DWORD dwRecordServerId, DWORD dwElapse, LPCTSTR lpRecordFileName, LPVOID lpUserValue);
// ������¼��ص��������壨��չ��
typedef void (CALLBACK * BRAS_OnServerRecordEx_CallBack)(DWORD dwUserId, LPCTSTR lpFileName, DWORD dwElapse, DWORD dwFlags, DWORD dwParam, LPCTSTR lpUserStr, DWORD dwRecordServerId, LPVOID lpUserValue);
// ��Ƶͨ����Ϣ֪ͨ�ص���������
typedef DWORD (CALLBACK * BRAS_OnVideoCallEvent_CallBack)(DWORD dwEventType, DWORD dwSrcUserId, DWORD dwTarUserId, DWORD dwErrorCode, DWORD dwFlags, DWORD dwParam, LPCTSTR lpUserStr, LPVOID lpUserValue);


/**
 *	API ��������
 */
// ���÷�����Ӧ�ó�����Ϣ�ص�����
BRAS_API DWORD BRAS_SetOnServerAppMessageCallBack(BRAS_OnServerAppMessage_CallBack lpFunction, LPVOID lpUserValue=NULL);
// ���÷�����Ӧ�ó�����Ϣ����չ���ص�����
BRAS_API DWORD BRAS_SetOnServerAppMessageExCallBack(BRAS_OnServerAppMessageEx_CallBack lpFunction, LPVOID lpUserValue=NULL);
// ����SDK��ʱ���ص�������dwElapse����ʱ���������λ��ms��
BRAS_API DWORD BRAS_SetTimerEventCallBack(DWORD dwElapse, BRAS_OnTimerEvent_CallBack lpFunction, LPVOID lpUserValue=NULL);

// �����û������֤�ص�����
BRAS_API DWORD BRAS_SetVerifyUserCallBack(BRAS_VerifyUser_CallBack lpFunction, LPVOID lpUserValue=NULL);
// �����û�������뷿��ص�����
BRAS_API DWORD BRAS_SetPrepareEnterRoomCallBack(BRAS_PrepareEnterRoom_CallBack lpFunction, LPVOID lpUserValue=NULL);
// �����û���¼�ɹ��ص�����
BRAS_API DWORD BRAS_SetOnUserLoginActionCallBack(BRAS_OnUserLoginAction_CallBack lpFunction, LPVOID lpUserValue=NULL);
// �����û�ע���ص�����
BRAS_API DWORD BRAS_SetOnUserLogoutActionCallBack(BRAS_OnUserLogoutAction_CallBack lpFunction, LPVOID lpUserValue=NULL);
// �����û�ע����չ�ص�����
BRAS_API DWORD BRAS_SetOnUserLogoutActionExCallBack(BRAS_OnUserLogoutActionEx_CallBack lpFunction, LPVOID lpUserValue=NULL);
// �����û����뷿��ص�����
BRAS_API DWORD BRAS_SetOnUserEnterRoomActionCallBack(BRAS_OnUserEnterRoomAction_CallBack lpFunction, LPVOID lpUserValue=NULL);
// �����û��뿪����ص�����
BRAS_API DWORD BRAS_SetOnUserLeaveRoomActionCallBack(BRAS_OnUserLeaveRoomAction_CallBack lpFunction, LPVOID lpUserValue=NULL);
// �����û��ϲ�ҵ���Զ������ݻص�����
BRAS_API DWORD BRAS_SetOnRecvUserFilterDataCallBack(BRAS_OnRecvUserFilterData_CallBack lpFunction, LPVOID lpUserValue=NULL);
// �����û���������ͨ�����ݻص�����
BRAS_API DWORD BRAS_SetOnRecvUserTextMsgCallBack(BRAS_OnRecvUserTextMsg_CallBack lpFunction, LPVOID lpUserValue=NULL);
// ����͸��ͨ�����ݻص�����
BRAS_API DWORD BRAS_SetOnTransBufferCallBack(BRAS_OnTransBuffer_CallBack lpFunction, LPVOID lpUserValue=NULL);
// ����͸��ͨ��������չ�ص�����
BRAS_API DWORD BRAS_SetOnTransBufferExCallBack(BRAS_OnTransBufferEx_CallBack lpFunction, LPVOID lpUserValue=NULL);
// �����ļ�����ص�����
BRAS_API DWORD BRAS_SetOnTransFileCallBack(BRAS_OnTransFile_CallBack lpFunction, LPVOID lpUserValue=NULL);
// ���÷�����¼��֪ͨ�ص�����
BRAS_API DWORD BRAS_SetOnServerRecordCallBack(BRAS_OnServerRecord_CallBack lpFunction, LPVOID lpUserValue=NULL);
// ������Ƶͨ����Ϣ֪ͨ�ص�����
BRAS_API DWORD BRAS_SetOnVideoCallEventCallBack(BRAS_OnVideoCallEvent_CallBack lpFunction, LPVOID lpUserValue=NULL);
// ���ûص�����
BRAS_API DWORD BRAS_SetCallBack(DWORD dwCBType, LPVOID lpFunction, LPVOID lpUserValue=NULL);


// ��ȡSDK�汾��Ϣ
BRAS_API DWORD BRAS_GetSDKVersion(DWORD& dwMainVer, DWORD& dwSubVer, TCHAR* lpCompileTime, DWORD dwBufLen);
// ��ʼ��SDK
BRAS_API DWORD BRAS_InitSDK(DWORD dwReserved);
// �ͷ���Դ
BRAS_API DWORD BRAS_Release(void);

// ��ָ���û���������
BRAS_API DWORD BRAS_SendBufToUser(DWORD dwUserId, LPCTSTR lpBuf, DWORD dwLen);
// ��ָ������������û���������
BRAS_API DWORD BRAS_SendBufToRoom(DWORD dwRoomId, LPCTSTR lpBuf, DWORD dwLen);

// ��ָ���û�����͸��ͨ������
BRAS_API DWORD BRAS_TransBuffer(DWORD dwUserId, LPBYTE lpBuf, DWORD dwLen);
// ��ָ���û�������չ����������
BRAS_API DWORD BRAS_TransBufferEx(DWORD dwUserId, LPBYTE lpBuf, DWORD dwLen, DWORD wParam, DWORD lParam, DWORD dwFlags, DWORD& dwTaskId);
// ��ָ���û������ļ�
BRAS_API DWORD BRAS_TransFile(DWORD dwUserId, LPCTSTR lpLocalPathName, DWORD wParam, DWORD lParam, DWORD dwFlags, DWORD& dwTaskId);

// ���Ķ�¼�����
BRAS_API DWORD BRAS_StreamRecordCtrl(DWORD dwUserId, BOOL bStartRecord, DWORD dwFlags, DWORD dwParam, DWORD dwRecordServerId);
// ���Ķ�¼����ƣ���չ��
BRAS_API DWORD BRAS_StreamRecordCtrlEx(DWORD dwUserId, BOOL bStartRecord, DWORD dwFlags, DWORD dwParam, LPCTSTR lpUserStr=NULL, DWORD dwRecordServerId=-1);
// ����͸��ͨ�����ݸ�¼�������
BRAS_API DWORD BRAS_TransBuffer2RecordServer(DWORD dwUserId, LPBYTE lpBuf, DWORD dwLen, DWORD dwParam, DWORD dwRecordServerId);

// ��Ƶ�����¼����ƣ����󡢻ظ����Ҷϵȣ�
BRAS_API DWORD BRAS_VideoCallControl(DWORD dwEventType, DWORD dwUserId, DWORD dwErrorCode, DWORD dwFlags=0, DWORD dwParam=0, LPCTSTR lpUserStr=NULL);

// �����û�����ϸ��Ϣ
BRAS_API DWORD BRAS_SetUserInfo(DWORD dwUserId, DWORD dwInfoId, LPCTSTR lpInfoValue, DWORD dwFlags=0);
// ��ȡ�û�����ϸ��Ϣ
BRAS_API DWORD BRAS_GetUserInfo(DWORD dwUserId, DWORD dwInfoId, TCHAR* lpInfoValue, DWORD dwSize);
// �û���Ϣ����
BRAS_API DWORD BRAS_UserInfoControl(DWORD dwUserId, DWORD dwCtrlCode, DWORD wParam=0, DWORD lParam=0, LPCTSTR lpStrValue=NULL);

// SDK�ں˲�������
BRAS_API DWORD BRAS_SetSDKOption(DWORD optname, CHAR* optval, DWORD optlen);

#endif //BR_ANYCHAT_SERVER_SDK_H__INCLUDED_
