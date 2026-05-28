import { Component, inject, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { environment } from '../../environments/environments';

interface CourseItem {
    id: number;
    title: string;
    description: string;
    price: number;
    type: string;
    paymentMethod: string;
    isDigital: boolean;
    digitalFileName: string | null;
    course: { id: number; name: string; code: string };
}

@Component({
    selector: 'app-course-page',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './course-page.html',
    styleUrls: ['./course-page.css']
})
export class CoursePageComponent implements OnInit {
    private http = inject(HttpClient);
    private route = inject(ActivatedRoute);
    private router = inject(Router);

    items: CourseItem[] = [];
    courseName = '';

    ngOnInit() {
        this.route.params.subscribe(params => {
            const id = +params['id'];
            this.loadItems(id);
        });
    }

    loadItems(courseId: number) {
        this.http.get<CourseItem[]>(`${environment.apiBaseUrl}/api/courses/${courseId}/items`)
            .subscribe({
                next: (data) => {
                    this.items = data;
                    if (data.length > 0) {
                        this.courseName = data[0].course.name;
                    }
                },
                error: (err) => {
                    console.error('Failed to load course items', err);
                    this.items = [];
                }
            });
    }

    viewItem(id: number) {
        this.router.navigate(['/item', id]);
    }
}