import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { backendUrl } from '../../environments/environment';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class FileService {
  constructor(private readonly http: HttpClient) { }

  getFile(id: string): Promise<File> {
    return this.getById(id).toPromise().then(blob => {
      const fileName = 'preview.png';
      if (!blob) {
        throw new Error('Blob is undefined');
      }
      return new File([blob], fileName, { type: blob.type });
    });
  }

  private getById(id: string): Observable<Blob> {
    return this.http.get(`${backendUrl}/api/files/?id=${id}`, { responseType: 'blob' });
  }
}
