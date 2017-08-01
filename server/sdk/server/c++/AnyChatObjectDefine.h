#if !defined(_ANYCHAT_OBJECTDEFINE_H_INCLUDED_)
#define _ANYCHAT_OBJECTDEFINE_H_INCLUDED_

// �������Ͷ���
#define ANYCHAT_OBJECT_TYPE_AREA			4	///< ��������
#define ANYCHAT_OBJECT_TYPE_QUEUE			5	///< ���ж���
#define ANYCHAT_OBJECT_TYPE_AGENT			6	///< �ͷ�����
#define ANYCHAT_OBJECT_TYPE_CLIENTUSER		8	///< �ͻ����û������������������������

// ͨ�ñ�ʶ����
#define ANYCHAT_OBJECT_FLAGS_CLIENT			0	///< ��ͨ�ͻ�
#define ANYCHAT_OBJECT_FLAGS_AGENT			2	///< ��ϯ�û�
#define ANYCHAT_OBJECT_FLAGS_MANANGER		4	///< �����û�

#define ANYCHAT_INVALID_OBJECT_ID	((DWORD)-1)	///< ��Ч�Ķ���ID

// ��ϯ����״̬����
#define ANYCHAT_AGENT_STATUS_CLOSEED		0	///< �رգ��������ṩ����
#define ANYCHAT_AGENT_STATUS_WAITTING		1	///< �ȴ��У�����ʱ�����û�����
#define ANYCHAT_AGENT_STATUS_WORKING		2	///< �����У�����Ϊ�û�����
#define ANYCHAT_AGENT_STATUS_PAUSED			3	///< ��ͣ����




/**
 *	�������Զ���
 */

// ���󹫹���Ϣ���Ͷ���
#define ANYCHAT_OBJECT_INFO_FLAGS			7	///< �������Ա�־
#define ANYCHAT_OBJECT_INFO_NAME			8	///< ��������
#define ANYCHAT_OBJECT_INFO_PRIORITY		9	///< �������ȼ�
#define ANYCHAT_OBJECT_INFO_ATTRIBUTE		10	///< ����ҵ������
#define ANYCHAT_OBJECT_INFO_DESCRIPTION		11	///< ��������
#define ANYCHAT_OBJECT_INFO_INTTAG			12	///< �����ǩ�����ͣ��ϲ�Ӧ���Զ���
#define ANYCHAT_OBJECT_INFO_STRINGTAG		13	///< �����ǩ���ַ������ϲ�Ӧ���Զ���


// ����������Ϣ���Ͷ���
#define ANYCHAT_AREA_INFO_AGENTCOUNT		401	///< ��������ͷ��û���
#define ANYCHAT_AREA_INFO_GUESTCOUNT		402	///< ���������ڷÿ͵��û�����û��������е��û���
#define ANYCHAT_AREA_INFO_QUEUEUSERCOUNT	403	///< �����������Ŷӵ��û���
#define ANYCHAT_AREA_INFO_QUEUECOUNT		404	///< ���������ڶ��е�����

// ����״̬��Ϣ���Ͷ���
#define ANYCHAT_QUEUE_INFO_MYSEQUENCENO		501	///< �Լ��ڸö����е����
#define ANYCHAT_QUEUE_INFO_BEFOREUSERNUM	502	///< �����Լ�ǰ	����û���
#define ANYCHAT_QUEUE_INFO_MYENTERQUEUETIME	503	///< ������е�ʱ��
#define ANYCHAT_QUEUE_INFO_LENGTH			504	///< ���г��ȣ��ж��������Ŷӣ�������
#define ANYCHAT_QUEUE_INFO_WAITTIMESECOND	508	///< �Լ��ڶ����еĵȴ�ʱ�䣨�Ŷ�ʱ��������λ����


// �ͷ�״̬��Ϣ���Ͷ���
#define ANYCHAT_AGENT_INFO_SERVICESTATUS	601	///< ����״̬������
#define ANYCHAT_AGENT_INFO_SERVICEUSERID	602	///< ��ǰ������û�ID������
#define ANYCHAT_AGENT_INFO_SERVICEBEGINTIME	603	///< ��ǰ����Ŀ�ʼʱ�䣬����
#define ANYCHAT_AGENT_INFO_SERVICETOTALTIME	604	///< �ۼƷ���ʱ�䣬���ͣ���λ����
#define ANYCHAT_AGENT_INFO_SERVICETOTALNUM	605	///< �ۼƷ�����û���������




/**
 *	���󷽷�����
 */

// ���󹫹��������Ƴ�������
#define ANYCHAT_OBJECT_CTRL_CREATE			2	///< ����һ������
#define ANYCHAT_OBJECT_CTRL_SYNCDATA		3	///< ͬ���������ݸ�ָ���û���dwObjectId=-1����ʾͬ�������͵����ж���
#define ANYCHAT_OBJECT_CTRL_DEBUGOUTPUT		4	///< ���������Ϣ���

// ����������Ƴ�������
#define ANYCHAT_AREA_CTRL_USERENTER			401	///< �����������
#define ANYCHAT_AREA_CTRL_USERLEAVE			402	///< �뿪��������

// ���в������Ƴ�������
#define ANYCHAT_QUEUE_CTRL_USERENTER		501	///< �������
#define ANYCHAT_QUEUE_CTRL_USERLEAVE		502	///< �뿪����

// �ͷ��������Ƴ�������
#define ANYCHAT_AGENT_CTRL_SERVICESTATUS	601	///< ��ϯ����״̬���ƣ���ͣ���񡢹����С��رգ�
#define ANYCHAT_AGENT_CTRL_SERVICEREQUEST	602	///< ��������
#define ANYCHAT_AGENT_CTRL_FINISHSERVICE	604	///< ��������
#define ANYCHAT_AGENT_CTRL_EVALUATION		605	///< �������ۣ�wParamΪ�ͷ�userid��lParamΪ���֣�lpStrValueΪ����






/**
 *	�����첽�¼�����
 */

// ���󹫹��¼���������
#define ANYCHAT_OBJECT_EVENT_UPDATE			1	///< �������ݸ���
#define ANYCHAT_OBJECT_EVENT_SYNCDATAFINISH	2	///< ��������ͬ������

// ���������¼���������
#define ANYCHAT_AREA_EVENT_STATUSCHANGE		401	///< ��������״̬�仯
#define ANYCHAT_AREA_EVENT_ENTERRESULT		402	///< �������������
#define ANYCHAT_AREA_EVENT_USERENTER		403	///< �û������������
#define ANYCHAT_AREA_EVENT_USERLEAVE		404	///< �û��뿪��������
#define ANYCHAT_AREA_EVENT_LEAVERESULT		405	///< �뿪����������


// �����¼���������
#define ANYCHAT_QUEUE_EVENT_STATUSCHANGE	501	///< ����״̬�仯
#define ANYCHAT_QUEUE_EVENT_ENTERRESULT		502	///< ������н��
#define ANYCHAT_QUEUE_EVENT_USERENTER		503	///< �û��������
#define ANYCHAT_QUEUE_EVENT_USERLEAVE		504	///< �û��뿪����
#define ANYCHAT_QUEUE_EVENT_LEAVERESULT		505	///< �뿪���н��


// ��ϯ�¼���������
#define ANYCHAT_AGENT_EVENT_STATUSCHANGE	601	///< ��ϯ״̬�仯
#define ANYCHAT_AGENT_EVENT_SERVICENOTIFY	602	///< ��ϯ����֪ͨ���ĸ��û����ĸ��ͷ�����ҵ��
#define ANYCHAT_AGENT_EVENT_WAITINGUSER		603	///< ��ʱû�пͻ�����ȴ�




#endif // !defined(_ANYCHAT_OBJECTDEFINE_H_INCLUDED_)
