// dllmain.cpp : Defines the entry point for the DLL application.
#include "stdafx.h"
#include "jni.h"
#include <stdlib.h>
#include <string>
#include <windows.h>

#ifdef __cplusplus
extern "C" {
#endif

	BOOL APIENTRY DllMain(HMODULE hModule,
		DWORD  ul_reason_for_call,
		LPVOID lpReserved
	)
	{
		switch (ul_reason_for_call)
		{
		case DLL_PROCESS_ATTACH:
		case DLL_THREAD_ATTACH:
		case DLL_THREAD_DETACH:
		case DLL_PROCESS_DETACH:
			break;
		}
		return TRUE;
	}
	
	void GetJStringContent(JNIEnv *AEnv, jstring AStr, std::string &ARes) {
		if (!AStr) {
			ARes.clear();
			return;
		}
		const char *s = AEnv->GetStringUTFChars(AStr, NULL);
		ARes = s;
		AEnv->ReleaseStringUTFChars(AStr, s);
	}

	bool GetColor(short &ret) {
		CONSOLE_SCREEN_BUFFER_INFO info;
		if (!GetConsoleScreenBufferInfo(GetStdHandle(STD_OUTPUT_HANDLE), &info))
			return false;
		ret = info.wAttributes;
		return true;
	}

	JNIEXPORT void JNICALL Java_org_midnightas_gamebatch_GameBatchConsole_clear(JNIEnv *env, jclass theclass) {
		system("cls");
	}

	JNIEXPORT void JNICALL Java_org_midnightas_gamebatch_GameBatchConsole_setCursorPos(JNIEnv *env, jclass theclass, jint x, jint y) {
		COORD coord;
		coord.X = (int)x;
		coord.Y = (int)y;
		SetConsoleCursorPosition(GetStdHandle(STD_OUTPUT_HANDLE), coord);
	}

	JNIEXPORT jintArray JNICALL Java_org_midnightas_gamebatch_GameBatchConsole_getConsoleSize(JNIEnv *env, jclass theclass) {
		CONSOLE_SCREEN_BUFFER_INFO csbi;
		int columns, rows;
		GetConsoleScreenBufferInfo(GetStdHandle(STD_OUTPUT_HANDLE), &csbi);
		columns = csbi.srWindow.Right - csbi.srWindow.Left + 1;
		rows = csbi.srWindow.Bottom - csbi.srWindow.Top + 1;
		jintArray newarray;
		newarray = env->NewIntArray(2);
		jint thesize[] = { (jint)columns, (jint)rows };
		env->SetIntArrayRegion(newarray, 0, 2, thesize);
		return newarray;
	}

	JNIEXPORT void JNICALL Java_org_midnightas_gamebatch_GameBatchConsole_setPrintFG(JNIEnv *env, jclass theclass, jint color) {
		short col;
		GetColor(col);
		col = col - (col % 16);
		col = col + (int) color;
		SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE), col);
	}

	JNIEXPORT void JNICALL Java_org_midnightas_gamebatch_GameBatchConsole_setPrintBG(JNIEnv *env, jclass theclass, jint color) {
		short col;
		GetColor(col);
		short fg = col % 16;
		col = ((int)color * 16) + fg;
		SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE), col);
	}

#ifdef __cplusplus
}
#endif
