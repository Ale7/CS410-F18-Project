PGDMP         3                v            grade-manager    11.0    11.0 )    1           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                       false            2           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                       false            3           0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                       false            4           1262    16677    grade-manager    DATABASE     �   CREATE DATABASE "grade-manager" WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'English_United States.1252' LC_CTYPE = 'English_United States.1252';
    DROP DATABASE "grade-manager";
             postgres    false            �            1259    16688    category    TABLE     �   CREATE TABLE public.category (
    category_id integer NOT NULL,
    category_name character varying(100) NOT NULL,
    category_weight integer NOT NULL,
    course_id integer NOT NULL
);
    DROP TABLE public.category;
       public         postgres    false            �            1259    16686    category_category_id_seq    SEQUENCE     �   CREATE SEQUENCE public.category_category_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 /   DROP SEQUENCE public.category_category_id_seq;
       public       postgres    false    199            5           0    0    category_category_id_seq    SEQUENCE OWNED BY     U   ALTER SEQUENCE public.category_category_id_seq OWNED BY public.category.category_id;
            public       postgres    false    198            �            1259    16680    course    TABLE     '  CREATE TABLE public.course (
    course_id integer NOT NULL,
    course_class_num character varying(20) NOT NULL,
    course_term character varying(20) NOT NULL,
    course_year integer NOT NULL,
    course_section_num integer NOT NULL,
    course_description character varying(100) NOT NULL
);
    DROP TABLE public.course;
       public         postgres    false            �            1259    16678    course_course_id_seq    SEQUENCE     �   CREATE SEQUENCE public.course_course_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 +   DROP SEQUENCE public.course_course_id_seq;
       public       postgres    false    197            6           0    0    course_course_id_seq    SEQUENCE OWNED BY     M   ALTER SEQUENCE public.course_course_id_seq OWNED BY public.course.course_id;
            public       postgres    false    196            �            1259    16722    grade    TABLE        CREATE TABLE public.grade (
    grade_score integer NOT NULL,
    item_id integer NOT NULL,
    student_id integer NOT NULL
);
    DROP TABLE public.grade;
       public         postgres    false            �            1259    16703    item    TABLE     �   CREATE TABLE public.item (
    item_id integer NOT NULL,
    item_point_value integer NOT NULL,
    item_description text NOT NULL,
    item_name character varying(100) NOT NULL,
    category_id integer NOT NULL
);
    DROP TABLE public.item;
       public         postgres    false            �            1259    16701    item_item_id_seq    SEQUENCE     �   CREATE SEQUENCE public.item_item_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 '   DROP SEQUENCE public.item_item_id_seq;
       public       postgres    false    201            7           0    0    item_item_id_seq    SEQUENCE OWNED BY     E   ALTER SEQUENCE public.item_item_id_seq OWNED BY public.item.item_id;
            public       postgres    false    200            �            1259    16717    student    TABLE     �   CREATE TABLE public.student (
    student_id integer NOT NULL,
    student_username character varying(100) NOT NULL,
    student_name character varying(100) NOT NULL
);
    DROP TABLE public.student;
       public         postgres    false            �            1259    16737    student_enrolls_course    TABLE     p   CREATE TABLE public.student_enrolls_course (
    student_id integer NOT NULL,
    course_id integer NOT NULL
);
 *   DROP TABLE public.student_enrolls_course;
       public         postgres    false            �
           2604    16691    category category_id    DEFAULT     |   ALTER TABLE ONLY public.category ALTER COLUMN category_id SET DEFAULT nextval('public.category_category_id_seq'::regclass);
 C   ALTER TABLE public.category ALTER COLUMN category_id DROP DEFAULT;
       public       postgres    false    198    199    199            �
           2604    16683    course course_id    DEFAULT     t   ALTER TABLE ONLY public.course ALTER COLUMN course_id SET DEFAULT nextval('public.course_course_id_seq'::regclass);
 ?   ALTER TABLE public.course ALTER COLUMN course_id DROP DEFAULT;
       public       postgres    false    196    197    197            �
           2604    16706    item item_id    DEFAULT     l   ALTER TABLE ONLY public.item ALTER COLUMN item_id SET DEFAULT nextval('public.item_item_id_seq'::regclass);
 ;   ALTER TABLE public.item ALTER COLUMN item_id DROP DEFAULT;
       public       postgres    false    201    200    201            )          0    16688    category 
   TABLE DATA               Z   COPY public.category (category_id, category_name, category_weight, course_id) FROM stdin;
    public       postgres    false    199   10       '          0    16680    course 
   TABLE DATA                  COPY public.course (course_id, course_class_num, course_term, course_year, course_section_num, course_description) FROM stdin;
    public       postgres    false    197   u0       -          0    16722    grade 
   TABLE DATA               A   COPY public.grade (grade_score, item_id, student_id) FROM stdin;
    public       postgres    false    203   1       +          0    16703    item 
   TABLE DATA               c   COPY public.item (item_id, item_point_value, item_description, item_name, category_id) FROM stdin;
    public       postgres    false    201   h1       ,          0    16717    student 
   TABLE DATA               M   COPY public.student (student_id, student_username, student_name) FROM stdin;
    public       postgres    false    202   	2       .          0    16737    student_enrolls_course 
   TABLE DATA               G   COPY public.student_enrolls_course (student_id, course_id) FROM stdin;
    public       postgres    false    204   �2       8           0    0    category_category_id_seq    SEQUENCE SET     F   SELECT pg_catalog.setval('public.category_category_id_seq', 3, true);
            public       postgres    false    198            9           0    0    course_course_id_seq    SEQUENCE SET     B   SELECT pg_catalog.setval('public.course_course_id_seq', 6, true);
            public       postgres    false    196            :           0    0    item_item_id_seq    SEQUENCE SET     >   SELECT pg_catalog.setval('public.item_item_id_seq', 7, true);
            public       postgres    false    200            �
           2606    16695 -   category category_category_name_course_id_key 
   CONSTRAINT     |   ALTER TABLE ONLY public.category
    ADD CONSTRAINT category_category_name_course_id_key UNIQUE (category_name, course_id);
 W   ALTER TABLE ONLY public.category DROP CONSTRAINT category_category_name_course_id_key;
       public         postgres    false    199    199            �
           2606    16693    category category_pkey 
   CONSTRAINT     ]   ALTER TABLE ONLY public.category
    ADD CONSTRAINT category_pkey PRIMARY KEY (category_id);
 @   ALTER TABLE ONLY public.category DROP CONSTRAINT category_pkey;
       public         postgres    false    199            �
           2606    16685    course course_pkey 
   CONSTRAINT     W   ALTER TABLE ONLY public.course
    ADD CONSTRAINT course_pkey PRIMARY KEY (course_id);
 <   ALTER TABLE ONLY public.course DROP CONSTRAINT course_pkey;
       public         postgres    false    197            �
           2606    16726    grade grade_pkey 
   CONSTRAINT     _   ALTER TABLE ONLY public.grade
    ADD CONSTRAINT grade_pkey PRIMARY KEY (student_id, item_id);
 :   ALTER TABLE ONLY public.grade DROP CONSTRAINT grade_pkey;
       public         postgres    false    203    203            �
           2606    16711    item item_pkey 
   CONSTRAINT     Q   ALTER TABLE ONLY public.item
    ADD CONSTRAINT item_pkey PRIMARY KEY (item_id);
 8   ALTER TABLE ONLY public.item DROP CONSTRAINT item_pkey;
       public         postgres    false    201            �
           2606    16741 2   student_enrolls_course student_enrolls_course_pkey 
   CONSTRAINT     �   ALTER TABLE ONLY public.student_enrolls_course
    ADD CONSTRAINT student_enrolls_course_pkey PRIMARY KEY (student_id, course_id);
 \   ALTER TABLE ONLY public.student_enrolls_course DROP CONSTRAINT student_enrolls_course_pkey;
       public         postgres    false    204    204            �
           2606    16721    student student_pkey 
   CONSTRAINT     Z   ALTER TABLE ONLY public.student
    ADD CONSTRAINT student_pkey PRIMARY KEY (student_id);
 >   ALTER TABLE ONLY public.student DROP CONSTRAINT student_pkey;
       public         postgres    false    202            �
           2606    16696     category category_course_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.category
    ADD CONSTRAINT category_course_id_fkey FOREIGN KEY (course_id) REFERENCES public.course(course_id);
 J   ALTER TABLE ONLY public.category DROP CONSTRAINT category_course_id_fkey;
       public       postgres    false    197    199    2714            �
           2606    16727    grade grade_item_id_fkey    FK CONSTRAINT     {   ALTER TABLE ONLY public.grade
    ADD CONSTRAINT grade_item_id_fkey FOREIGN KEY (item_id) REFERENCES public.item(item_id);
 B   ALTER TABLE ONLY public.grade DROP CONSTRAINT grade_item_id_fkey;
       public       postgres    false    201    203    2720            �
           2606    16732    grade grade_student_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.grade
    ADD CONSTRAINT grade_student_id_fkey FOREIGN KEY (student_id) REFERENCES public.student(student_id);
 E   ALTER TABLE ONLY public.grade DROP CONSTRAINT grade_student_id_fkey;
       public       postgres    false    202    203    2722            �
           2606    16712    item item_category_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.item
    ADD CONSTRAINT item_category_id_fkey FOREIGN KEY (category_id) REFERENCES public.category(category_id);
 D   ALTER TABLE ONLY public.item DROP CONSTRAINT item_category_id_fkey;
       public       postgres    false    201    199    2718            �
           2606    16747 <   student_enrolls_course student_enrolls_course_course_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.student_enrolls_course
    ADD CONSTRAINT student_enrolls_course_course_id_fkey FOREIGN KEY (course_id) REFERENCES public.course(course_id);
 f   ALTER TABLE ONLY public.student_enrolls_course DROP CONSTRAINT student_enrolls_course_course_id_fkey;
       public       postgres    false    2714    204    197            �
           2606    16742 =   student_enrolls_course student_enrolls_course_student_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.student_enrolls_course
    ADD CONSTRAINT student_enrolls_course_student_id_fkey FOREIGN KEY (student_id) REFERENCES public.student(student_id);
 g   ALTER TABLE ONLY public.student_enrolls_course DROP CONSTRAINT student_enrolls_course_student_id_fkey;
       public       postgres    false    2722    204    202            )   4   x�3����M-�/��42�4�2�,ͬ�J-�4q�9CR�K�9MA�=... Fj�      '   �   x�3�t614�tK���420��4�tI,ILJ,N-�2���:��e�d�s��Ф�+�R��S�K��*�L�J�9��2��A�́��S�\R�Rs�rS�J�L��2�Peu+�Y���qqq ��7�      -   Q   x�U̻�PD��q��ҋ��!��3{AAJ�m�[�5�P�6�d��]�f����P�5rxV�������VR�Os���8�      +   �   x�M��� ���W���k{�Gb/^��ȥ5@M��F��y��a(��M�	3�|���`R�uӵ���^lz�*���7G���.�A%э9N�^x�0UKXw;:?��Uh���޲��E�SF�д��$�J�,��^iw0�      ,   l   x�%Ƚ
�0 ���)� ��G{w�Y]�.��R��X|{���8�OK���Y�<̥����jh�l��cðԘגg�!ĺ��U�'�N��خ=D�`*��jl��ocS�      .   -   x�37�42�034�4�04��4�0� ��M,-�-L-��=... ���     